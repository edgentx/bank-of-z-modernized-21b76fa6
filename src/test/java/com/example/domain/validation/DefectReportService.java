package com.example.domain.validation;

import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;

/**
 * Wrapper service mimicking the 'temporal-worker exec' logic.
 * This class wires the GitHub creation and Slack notification logic.
 * In a real scenario, this might be a Temporal Activity implementation.
 */
public class DefectReportService {

    private final GitHubIssueTracker gitHub;
    private final SlackNotifier slack;

    public DefectReportService(GitHubIssueTracker gitHub, SlackNotifier slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    public void reportDefect(String title, String body, String label) {
        // Step 1: Create GitHub Issue
        String issueUrl = gitHub.createIssue(title, body, label);

        // Step 2: Notify Slack
        // BUG SCENARIO: The defect states that 'issueUrl' might not be included here.
        // We are testing to ensure it IS included.
        String slackMessage = String.format("Defect Reported: %s. GitHub URL: %s", title, issueUrl);
        slack.sendNotification(slackMessage);
    }
}
