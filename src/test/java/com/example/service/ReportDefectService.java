package com.example.service;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service/Activity implementation for reporting defects.
 * This class represents the 'Code Under Test' for the S-FB-1 story.
 * In a real scenario, this would likely be the implementation of the Temporal Activity interface.
 */
public class ReportDefectService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting flow:
     * 1. Create GitHub Issue.
     * 2. Post notification to Slack containing the GitHub URL.
     * 
     * @param title The defect title
     * @param description The defect description
     * @param reproduction The reproduction steps
     * @return true if successful
     */
    public boolean executeReportDefect(String title, String description, String reproduction) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        // Step 1: Create Issue
        String body = description + "\n\nReproduction:\n" + reproduction;
        String gitHubUrl = gitHubPort.createIssue(title, body);

        // Step 2: Notify Slack
        // Defect VW-454: Verify this body includes the link
        String slackBody = "New Defect Reported: " + title + "\nGitHub Issue: " + gitHubUrl;
        return slackNotificationPort.postMessage(slackBody);
    }
}
