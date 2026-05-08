package com.example.application;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface defining the operations performed by the Workflow.
 */
@ActivityInterface
public interface DefectReportingActivity {

    /**
     * Creates an issue in GitHub.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The URL of the created issue.
     */
    String createGitHubIssue(String title, String body);

    /**
     * Sends a notification to Slack containing the GitHub Issue URL.
     * Defect VW-454 Validation: The body must contain the GitHub URL.
     *
     * @param githubUrl The URL to include in the Slack message.
     */
    void notifySlack(String githubUrl);
}