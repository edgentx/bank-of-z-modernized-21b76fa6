package com.example.application;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.workers.ReportDefectActivity;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Orchestrates the reporting of defects via Temporal.
 * Ensures that if the Slack notification fails, the workflow can retry based on Temporal policies.
 */
@WorkflowInterface
public interface DefectReportingWorkflow {

    @WorkflowMethod
    void reportDefect(ReportDefectCmd cmd);

    /**
     * Workflow implementation.
     */
    class DefectReportingWorkflowImpl implements DefectReportingWorkflow {
        private final ReportDefectActivity activity;

        // Temporal requires a no-arg constructor or factory for the Workflow class,
        // but Activities are injected via the Worker stub in practice.
        // For this Spring implementation, we assume the stub is proxied.
        public DefectReportingWorkflowImpl() {
            // Activities are initialized by the Temporal Worker using a stub, 
            // but we declare the type for clarity.
            this.activity = io.temporal.workflow.Workflow.newActivityStub(ReportDefectActivity.class);
        }

        @Override
        public void reportDefect(ReportDefectCmd cmd) {
            activity.reportDefect(cmd);
        }
    }
}
