package com.vforce360.core;

import com.vforce360.ports.github.GitHubIssuePort;
import com.vforce360.ports.slack.SlackNotificationPort;

/**
 * Service class handling the logic for reporting defects.
 * This represents the implementation that will be fixed.
 * Currently, it is a stub that will fail the tests.
 */
public class DefectReportOrchestrator {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;
    private static final String SLACK_CHANNEL_ID = "C-vforce360-issues";

    public DefectReportOrchestrator(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * This is the method under test for S-FB-1.
     *
     * @param title The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String title, String description) {
        // Step 1: Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue("vforce360", "core", title, description);

        // Step 2: Notify Slack
        // Defect VW-454 implies the URL might be missing here.
        // We deliberately leave it out or format it incorrectly to ensure the test FAILS initially (Red Phase).
        String slackMessage = "Defect Reported: " + title + "\nDescription: " + description;
        // The URL is currently NOT attached to the message, causing the test to fail.
        
        slackNotificationPort.sendMessage(SLACK_CHANNEL_ID, slackMessage);
    }
}
