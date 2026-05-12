package com.example.infrastructure.temporal;

import com.example.infrastructure.temporal.saga.AccountOpeningInput;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingInput;
import com.example.infrastructure.temporal.workflow.AccountOpeningWorkflow;
import com.example.infrastructure.temporal.workflow.LegacyTransactionRouteWorkflow;
import com.example.ports.WorkflowException;
import com.example.ports.WorkflowExecutionHandle;
import com.example.ports.WorkflowOrchestrationPort;
import com.example.ports.WorkflowStatus;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.StatusUtils;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * BANK S-33 — {@link WorkflowOrchestrationPort} backed by Temporal.
 *
 * <p>Thin translation layer over {@link WorkflowClient}: starts workflows
 * by their logical type name, queries status via Temporal's
 * {@code DescribeWorkflowExecution} RPC, and issues cooperative cancellation
 * via the workflow stub. The adapter deliberately keeps the set of
 * supported workflow types tiny ({@code AccountOpeningWorkflow},
 * {@code LegacyTransactionRouteWorkflow}) so adding a new workflow is a
 * code change rather than a runtime config — surface area worth gating
 * through code review at this stage of the modernization.
 *
 * <p>All Temporal exceptions ({@code WorkflowServiceException},
 * {@code StatusRuntimeException}) are caught and re-thrown as
 * {@link WorkflowException} so application code never imports
 * {@code io.temporal.*} or {@code io.grpc.*}.
 */
@Component
public class TemporalWorkflowOrchestrationAdapter implements WorkflowOrchestrationPort {

  private final WorkflowClient client;
  private final TemporalProperties props;

  public TemporalWorkflowOrchestrationAdapter(WorkflowClient client, TemporalProperties props) {
    this.client = client;
    this.props = props;
  }

  @Override
  public WorkflowExecutionHandle startWorkflow(String workflowType, String workflowId, Object input) {
    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setTaskQueue(props.getTaskQueue())
        .setWorkflowId(workflowId)
        .setWorkflowExecutionTimeout(props.getWorkflowExecutionTimeout())
        .build();

    try {
      WorkflowExecution execution;
      switch (workflowType) {
        case "AccountOpeningWorkflow" -> {
          AccountOpeningWorkflow stub = client.newWorkflowStub(AccountOpeningWorkflow.class, options);
          execution = WorkflowClient.start(stub::openAccount, (AccountOpeningInput) input);
        }
        case "LegacyTransactionRouteWorkflow" -> {
          LegacyTransactionRouteWorkflow stub =
              client.newWorkflowStub(LegacyTransactionRouteWorkflow.class, options);
          execution = WorkflowClient.start(stub::route, (LegacyTransactionRoutingInput) input);
        }
        default -> throw new WorkflowException("Unknown workflow type: " + workflowType);
      }
      return new WorkflowExecutionHandle(execution.getWorkflowId(), execution.getRunId(), WorkflowStatus.RUNNING);
    } catch (WorkflowException e) {
      throw e;
    } catch (Exception e) {
      throw new WorkflowException("Failed to start workflow " + workflowType + "/" + workflowId, e);
    }
  }

  @Override
  public Optional<WorkflowStatus> queryStatus(String workflowId) {
    DescribeWorkflowExecutionRequest request = DescribeWorkflowExecutionRequest.newBuilder()
        .setNamespace(props.getNamespace())
        .setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId).build())
        .build();
    try {
      DescribeWorkflowExecutionResponse response = client.getWorkflowServiceStubs()
          .blockingStub()
          .describeWorkflowExecution(request);
      WorkflowExecutionStatus raw = response.getWorkflowExecutionInfo().getStatus();
      return Optional.of(mapStatus(raw));
    } catch (io.grpc.StatusRuntimeException e) {
      // NOT_FOUND on a missing workflow id is a normal "no such workflow"
      // signal, not a transport failure — return empty so callers can
      // distinguish absent vs reachable-but-erroring.
      if (StatusUtils.getFailure(e, io.temporal.api.errordetails.v1.NotFoundFailure.class) != null
          || e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
        return Optional.empty();
      }
      throw new WorkflowException("Failed to describe workflow " + workflowId, e);
    } catch (Exception e) {
      throw new WorkflowException("Failed to describe workflow " + workflowId, e);
    }
  }

  @Override
  public void cancelWorkflow(String workflowId) {
    try {
      WorkflowStub stub = client.newUntypedWorkflowStub(workflowId);
      stub.cancel();
    } catch (Exception e) {
      throw new WorkflowException("Failed to cancel workflow " + workflowId, e);
    }
  }

  /**
   * Translate the Temporal gRPC status enum into the provider-neutral port
   * status. {@code UNSPECIFIED} maps to {@link WorkflowStatus#RUNNING}
   * conservatively — a workflow with no recorded status is treated as still
   * in flight rather than reported as a fabricated terminal state.
   */
  static WorkflowStatus mapStatus(WorkflowExecutionStatus raw) {
    return switch (raw) {
      case WORKFLOW_EXECUTION_STATUS_COMPLETED -> WorkflowStatus.COMPLETED;
      case WORKFLOW_EXECUTION_STATUS_FAILED -> WorkflowStatus.FAILED;
      case WORKFLOW_EXECUTION_STATUS_CANCELED -> WorkflowStatus.CANCELLED;
      case WORKFLOW_EXECUTION_STATUS_TERMINATED -> WorkflowStatus.TERMINATED;
      case WORKFLOW_EXECUTION_STATUS_TIMED_OUT -> WorkflowStatus.TIMED_OUT;
      case WORKFLOW_EXECUTION_STATUS_CONTINUED_AS_NEW -> WorkflowStatus.CONTINUED_AS_NEW;
      case WORKFLOW_EXECUTION_STATUS_RUNNING, WORKFLOW_EXECUTION_STATUS_UNSPECIFIED, UNRECOGNIZED ->
          WorkflowStatus.RUNNING;
    };
  }
}
