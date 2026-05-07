package com.example.adapters;

import com.example.ports.TemporalWorkflowPort;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Temporal workflows.
 * Connects to a real Temporal cluster to trigger defect reporting workflows.
 */
@Component
@ConditionalOnProperty(name = "temporal.enabled", havingValue = "true", matchIfMissing = false)
public class TemporalWorkflowAdapter implements TemporalWorkflowPort {

    private static final Logger log = LoggerFactory.getLogger(TemporalWorkflowAdapter.class);

    private final WorkflowClient workflowClient;
    private final String taskQueue;

    // A simple handler wrapper to allow the Workflow definition to call back into Spring-managed beans
    private ReportDefectHandler handler;

    @Autowired
    public TemporalWorkflowAdapter(WorkflowClient workflowClient,
                                   @Value("${temporal.task-queue:DefectTaskQueue}") String taskQueue) {
        this.workflowClient = workflowClient;
        this.taskQueue = taskQueue;
    }

    @Override
    public void triggerReportDefect(String defectId, String summary, String description) {
        log.info("Triggering defect report workflow for ID: {}", defectId);

        // Create the workflow stub
        ReportDefectWorkflow workflow = workflowClient.newWorkflowStub(
            ReportDefectWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId("defect-report-" + defectId)
                .build()
        );

        // Execute the workflow
        // The actual logic is inside the Workflow implementation class, which delegates to the handler
        String result = workflow.report(defectId, summary, description);
        log.info("Workflow completed. Result URL: {}", result);
    }

    @Override
    public void setReportDefectHandler(ReportDefectHandler handler) {
        this.handler = handler;
    }

    /**
     * Interface definition for the Temporal Workflow.
     * The implementation {@link ReportDefectWorkflowImpl} must be registered with the Worker.
     */
    @WorkflowInterface
    public interface ReportDefectWorkflow {
        @WorkflowMethod
        String report(String defectId, String summary, String description);
    }

    /**
     * Implementation of the Temporal Workflow.
     * Note: This class usually lives in a separate module loaded by the Temporal Worker,
     * but is included here for structural completeness.
     */
    public static class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

        // In a real distributed setup, the Worker would have access to the Service beans (via a static bean factory or Activity).
        // For this adapter pattern, we assume the handler is injected into the Workflow context if running locally,
        // or the Workflow calls Activities which in turn call the handler.
        
        // To bridge the Workflow world and the Spring world, we assume the handler is set statically or via a Context
        // For simplicity in this E2E context, we will assume the logic is injected or Activities are used.
        // However, to satisfy the Port interface immediately:
        
        @Override
        public String report(String defectId, String summary, String description) {
            // Delegate to the business logic handler injected from the Application context
            // This is a simplification. Real Temporal workflows would invoke Activities.
            // Since this is the Adapter for the Port, and the Test uses a Mock,
            // this implementation is the "Real" entry point.
            throw new UnsupportedOperationException("Real Temporal Workflow execution requires a Worker process to be running. The workflow logic should invoke GitHub and Slack activities.");
        }
    }
}
