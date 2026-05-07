package com.example.workers;

import com.example.application.ReportDefectWorkflowService;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    void execute(String title, String description);

    static class ReportDefectWorkflowImpl implements ReportDefectWorkflow {
        private final ReportDefectWorkflowService service;

        public ReportDefectWorkflowImpl(ReportDefectWorkflowService service) {
            this.service = service;
        }

        @Override
        public void execute(String title, String description) {
            service.reportDefect(title, description);
        }
    }
}
