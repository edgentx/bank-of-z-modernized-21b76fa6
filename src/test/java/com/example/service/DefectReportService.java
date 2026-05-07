package com.example.service;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service to handle defect reporting logic.
 * This file is a placeholder/implementation target to make the TDD Green phase possible later.
 * Currently, it might just be an interface or a stub if we are strictly writing tests first,
 * but often in Java TDD we define the class we intend to implement.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportService(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(String title, String body) {
        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, body);

        // Step 2: Prepare Slack Body with URL (The fix for VW-454)
        String slackBody = "New defect reported: " + title + "\n" +
                           "GitHub Issue: " + issueUrl; // CRITICAL: This line was missing/implied broken

        // Step 3: Post to Slack
        slackPort.postMessage("#vforce360-issues", slackBody);
    }
}
