package com.example.ports;

/**
 * Port interface for Temporal workflow interactions.
 * Real implementation would trigger the Temporal workflow.
 * Test implementation orchestrates the flow synchronously for validation.
 */
public interface TemporalWorkflowPort {

    /**
     * Simulates triggering the report_defect workflow.
     * This orchestrates the logic that eventually calls the Slack port.
     * 
     * @param issueId The GitHub URL/ID of the issue being reported.
     */
    void executeReportDefectWorkflow(String issueId);
}
