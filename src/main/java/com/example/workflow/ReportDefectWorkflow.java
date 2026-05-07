package com.example.workflow;

/**
 * Workflow interface for reporting a defect.
 */
public interface ReportDefectWorkflow {
    /**
     * Reports a defect to GitHub and notifies Slack.
     * @param defectId The ID of the defect (e.g., VW-454).
     * @param description The description of the defect.
     */
    void reportDefect(String defectId, String description);
}
