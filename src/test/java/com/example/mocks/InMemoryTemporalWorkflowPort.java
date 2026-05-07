package com.example.mocks;

import com.example.ports.TemporalWorkflowPort;

/**
 * Mock implementation of TemporalWorkflowPort.
 * Allows triggering a workflow synchronously in memory for testing.
 */
public class InMemoryTemporalWorkflowPort implements TemporalWorkflowPort {

    private ReportDefectHandler handler;

    @Override
    public void triggerReportDefect(String defectId, String summary, String description) {
        if (handler == null) {
            throw new IllegalStateException("Workflow handler not configured for test");
        }
        // Execute the workflow logic synchronously
        handler.handle(defectId, summary, description);
    }

    @Override
    public void setReportDefectHandler(ReportDefectHandler handler) {
        this.handler = handler;
    }
}
