package com.example.activities;

import com.example.adapters.GitHubRestAdapter;
import com.example.adapters.OkHttpSlackClient;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Implementation of the Defect Reporting activities.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification
 * containing the link to that issue.
 */
public class DefectReportingActivitiesImpl implements DefectReportingActivities {

    private final SlackPort slackClient;
    private final GitHubPort gitHubAdapter;

    public DefectReportingActivitiesImpl(SlackPort slackClient, GitHubPort gitHubAdapter) {
        this.slackClient = slackClient;
        this.gitHubAdapter = gitHubAdapter;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Creates an issue in GitHub.
     * 2. Formats a Slack message containing the GitHub URL.
     * 3. Publishes the message to Slack.
     */
    @Override
    public void reportDefect(String defectId, String title, String description) {
        // Step 1: Create GitHub Issue
        // Assuming labels based on defect ID or default behavior
        String[] labels = {"bug", " defect-reporting"};
        String githubUrl = gitHubAdapter.createIssue(defectId + ": " + title, description, labels);

        if (githubUrl == null || githubUrl.isEmpty()) {
            throw new IllegalStateException("Failed to retrieve GitHub issue URL.");
        }

        // Step 2: Format Slack Message (JSON payload)
        // Expected format for Slack API Incoming Webhook or Chat.PostMessage
        String slackPayload = String.format(
                "{\"text\": \"New defect reported: %s\nGitHub Issue: %s\"}",
                title, githubUrl
        );

        // Step 3: Publish to Slack
        slackClient.publish(slackPayload);
    }
}