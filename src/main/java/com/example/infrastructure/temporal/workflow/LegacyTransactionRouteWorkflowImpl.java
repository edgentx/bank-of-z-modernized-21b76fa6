package com.example.infrastructure.temporal.workflow;

import com.example.infrastructure.temporal.activity.LegacyBridgeActivities;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingInput;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingResult;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Workflow;
import java.time.Duration;

/**
 * BANK S-33 — Temporal workflow implementation for legacy CICS/IMS
 * transaction routing.
 *
 * <p>The workflow:
 * <ol>
 *   <li>asks the routing aggregate which leg to take ({@code MODERN} vs
 *       {@code LEGACY}),</li>
 *   <li>when LEGACY, invokes the CICS bridge activity under the configured
 *       retry policy so transient MQ/network blips do not surface to the
 *       caller,</li>
 *   <li>writes a sync checkpoint so the parity verifier has a known good
 *       offset to confirm dual-ledger agreement.</li>
 * </ol>
 *
 * <p>This workflow does not need compensation because the legacy CICS call
 * is the only side-effecting step and the checkpoint write is idempotent
 * — Temporal's retry policy is sufficient to handle transient failures.
 * The {@code @QueryMethod} still lets operators see which leg is in flight.
 */
public class LegacyTransactionRouteWorkflowImpl implements LegacyTransactionRouteWorkflow {

  private volatile String currentStep = "INITIALIZED";

  @Override
  public LegacyTransactionRoutingResult route(LegacyTransactionRoutingInput input) {
    LegacyBridgeActivities activities = buildActivityStub();

    try {
      currentStep = "EVALUATING_ROUTE";
      String chosenRoute = activities.evaluateRouting(
          input.routeId(), input.sourceSystem(), input.transactionType(),
          input.accountNumber(), input.amountCents(), input.currency());

      if ("LEGACY".equalsIgnoreCase(chosenRoute)) {
        currentStep = "INVOKING_CICS";
        activities.invokeCicsTransaction(
            input.routeId(), input.accountNumber(),
            input.transactionType(), input.amountCents(), input.currency());
      }

      currentStep = "RECORDING_CHECKPOINT";
      String checkpointId = "chkpt-" + input.routeId();
      activities.recordSyncCheckpoint(
          checkpointId, input.sourceSystem(),
          /* recordsProcessed = */ 1L,
          /* lastSequenceNumber = */ Workflow.currentTimeMillis());

      currentStep = "DONE";
      return LegacyTransactionRoutingResult.succeeded(input.routeId(), chosenRoute, checkpointId);
    } catch (ActivityFailure failure) {
      currentStep = "FAILED";
      String reason = failure.getCause() != null && failure.getCause().getMessage() != null
          ? failure.getCause().getMessage()
          : failure.getMessage();
      return LegacyTransactionRoutingResult.failed(input.routeId(), "UNKNOWN", reason);
    }
  }

  @Override
  public String currentStep() {
    return currentStep;
  }

  private LegacyBridgeActivities buildActivityStub() {
    RetryOptions retry = RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1))
        .setMaximumInterval(Duration.ofSeconds(30))
        .setBackoffCoefficient(2.0)
        .setMaximumAttempts(5)
        .build();

    ActivityOptions options = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(30))
        .setScheduleToCloseTimeout(Duration.ofMinutes(2))
        .setRetryOptions(retry)
        .build();

    return Workflow.newActivityStub(LegacyBridgeActivities.class, options);
  }
}
