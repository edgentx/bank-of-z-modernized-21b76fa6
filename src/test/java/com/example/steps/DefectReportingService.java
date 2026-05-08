package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Service class acting as the System Under Test (SUT).
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 * 
 * Implementation for Story VW-454 to ensure Slack body contains the GitHub URL.
 */
@Service
public class DefectReportingService {

    private final SlackNotifierPort slackNotifier;
    private final GitHubIssuePort gitHubClient;

    public DefectReportingService(SlackNotifierPort slackNotifier, GitHubIssuePort gitHubClient) {
        this.slackNotifier = slackNotifier;
        this.gitHubClient = gitHubClient;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * 
     * @param projectId The project ID associated with the defect
     * @param defectId The defect ID (e.g., VW-454)
     * @param description The description of the defect
     */
    public void reportDefect(String projectId, String defectId, String description) {
        // 1. Create GitHub Issue
        // Using defectId as the title and description as the body
        String issueUrl = gitHubClient.createIssue(defectId, description);

        // 2. Notify Slack
        // Ensure the message contains the GitHub issue URL as required by VW-454
        String message = "Defect Reported: " + defectId + "\nGitHub issue: " + issueUrl;

        slackNotifier.sendNotification(message);
    }
}
