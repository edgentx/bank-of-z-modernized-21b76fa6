package com.example;

/**
 * Workflow interface for reporting a defect.
 * This orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
public interface ReportDefectWorkflow {

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     * @return The URL of the created GitHub issue.
     */
    String execute(String title, String description);
}