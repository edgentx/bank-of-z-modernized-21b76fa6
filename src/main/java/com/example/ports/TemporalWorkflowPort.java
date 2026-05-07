package com.example.ports;

/**
 * Port for interacting with Temporal workflows.
 */
public interface TemporalWorkflowPort {
    
    /**
     * Triggers the _report_defect workflow.
     */
    void triggerReportDefect(String defectId, String summary, String description);

    /**
     * Internal handler interface for the workflow logic (Dependency Injection).
     */
    interface ReportDefectHandler {
        String handle(String defectId, String summary, String description);
    }

    void setReportDefectHandler(ReportDefectHandler handler);
}
