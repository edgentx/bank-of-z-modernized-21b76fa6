package com.example.ports;

public interface TemporalPort {
    /**
     * Execute the report_defect workflow
     * @param defectId The defect ID to report
     * @return true if workflow started successfully
     */
    boolean executeReportDefect(String defectId);
    
    /**
     * Get the status of a workflow execution
     * @param workflowId The workflow execution ID
     * @return The status string
     */
    String getWorkflowStatus(String workflowId);
}