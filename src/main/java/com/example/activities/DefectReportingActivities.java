package com.example.activities;

/**
 * Temporal Activity interface for reporting defects.
 * Wraps the interaction between GitHub creation and Slack notification.
 */
public interface DefectReportingActivities {
    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param defectId The ID of the defect (e.g., VW-454).
     * @param title The title of the defect.
     * @param description The description of the defect.
     * @throws Exception if the workflow fails.
     */
    void reportDefect(String defectId, String title, String description) throws Exception;
}