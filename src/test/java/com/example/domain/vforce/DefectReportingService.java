package com.example.domain.vforce;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Domain Service for handling defect reporting logic.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class DefectReportingService {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportingService(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(String title, String description, String channelId) {
        // Step 1: Create GitHub Issue
        // Note: This implementation is intentionally a placeholder (Red Phase)
        // The real implementation might need error handling or retry logic.
        String issueUrl = githubPort.createIssue(title, description);

        // Step 2: Notify Slack
        // The body MUST include the issueUrl (Target of the test)
        String slackBody = "New Defect Reported: " + title + "\n" +
                          "GitHub Issue: " + issueUrl; // Expected format

        slackPort.sendMessage(channelId, slackBody);
    }
}