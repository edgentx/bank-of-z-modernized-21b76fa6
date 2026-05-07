package com.example.workflows;

import io.temporal.workflow.ActivityInterface;

/**
 * Temporal Activity interface for reporting defects.
 * This decouples the workflow definition from the implementation.
 */
@ActivityInterface
public interface DefectReportActivities {

    /**
     * Creates a GitHub issue for the defect.
     *
     * @param title The defect title.
     * @param body  The defect description.
     * @return The URL of the created GitHub issue.
     */
    String createGitHubIssue(String title, String body);

    /**
     * Sends a notification to Slack including the GitHub URL.
     * FIX for VW-454: The URL must be present in the resulting Slack body.
     *
     * @param channel   The target Slack channel.
     * @param issueUrl  The URL to include in the message.
     */
    void notifySlack(String channel, String issueUrl);
}
