package com.example.workflow;

import java.net.URI;

/**
 * Workflow interface for reporting defects.
 */
public interface ReportDefectWorkflow {

    /**
     * Executes the report_defect workflow.
     * Creates a GitHub issue and then sends a Slack notification.
     *
     * @param defectId The ID of the defect (e.g. VW-454)
     * @param message  The validation error message
     */
    void execute(String defectId, String message);
}
