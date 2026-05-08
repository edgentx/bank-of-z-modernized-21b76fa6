package com.example.workflow;

/**
 * Workflow interface for reporting defects.
 * Temporal requires an interface for the Workflow stub.
 */
public interface ReportDefectWorkflow {

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title The title of the defect
     * @param body  The description of the defect
     * @return The URL of the created GitHub issue
     */
    String report(String title, String body);
}