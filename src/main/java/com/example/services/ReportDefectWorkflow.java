package com.example.services;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Orchestrator service for reporting defects.
 * Logic:
 * 1. Creates an issue via GitHubIssuePort.
 * 2. Posts a notification to Slack including the returned URL.
 * 
 * This implements the 'Green' phase logic for S-FB-1 / VW-454.
 */
@Service
public class ReportDefectWorkflow {

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting flow.
     * 
     * @param title The title of the defect
     * @param description The body of the defect
     * @param channel The target Slack channel (e.g., "#vforce360-issues")
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void report(String title, String description, String channel) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Defect title cannot be null or blank");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null or blank");
        }
        if (description == null) {
            // Description is allowed to be empty string, but not null for consistency
            description = "";
        }

        // 1. Create GitHub Issue
        String githubUrl = githubIssuePort.createIssue(title, description);

        // 2. Verify we got a URL back (Defensive programming for VW-454)
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalStateException("GitHub issue creation failed to return a valid URL");
        }

        // 3. Construct Slack Message ensuring URL is present
        // Note: Explicitly formatting the string to include the URL to satisfy VW-454
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title,
            githubUrl
        );

        // 4. Post to Slack
        slackNotificationPort.postMessage(channel, messageBody);
    }
}
