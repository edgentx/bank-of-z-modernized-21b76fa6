package com.example.steps;

import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubIssuePort;

/**
 * Service class acting as the System Under Test (SUT).
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 * 
 * CRITICAL: This implementation is currently a STUB/BROKEN to ensure the test FAILS (Red Phase).
 */
public class DefectReportingService {

    private final SlackNotifierPort slackNotifier;
    private final GitHubIssuePort gitHubClient;

    public DefectReportingService(SlackNotifierPort slackNotifier, GitHubIssuePort gitHubClient) {
        this.slackNotifier = slackNotifier;
        this.gitHubClient = gitHubClient;
    }

    public void reportDefect(String projectId, String defectId, String description) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubClient.createIssue(defectId, description);

        // 2. Notify Slack
        // INTENTIONAL BUG FOR RED PHASE:
        // Currently, this just sends the defect ID without the URL.
        // This causes the assertion 'capturedSlackBody.contains(testGitHubUrl)' to fail.
        String message = "Defect Reported: " + defectId;
        
        // The correct implementation would be:
        // String message = "Defect Reported: " + defectId + "\nGitHub issue: " + issueUrl;

        slackNotifier.sendNotification(message);
    }
}