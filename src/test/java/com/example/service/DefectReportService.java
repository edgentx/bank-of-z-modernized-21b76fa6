package com.example.service;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service to handle defect reporting logic.
 * Orchestrates the creation of a GitHub issue and subsequent notification via Slack.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportService(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating an issue on GitHub and notifying Slack.
     * 
     * @param title The title of the defect.
     * @param body The description of the defect.
     */
    public void reportDefect(String title, String body) {
        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, body);

        // Step 2: Prepare Slack Body with URL (The fix for VW-454)
        // Ensures the link is present in the notification body.
        String slackBody = "New defect reported: " + title + "\n" +
                           "GitHub Issue: " + issueUrl;

        // Step 3: Post to Slack
        slackPort.postMessage("#vforce360-issues", slackBody);
    }
}
