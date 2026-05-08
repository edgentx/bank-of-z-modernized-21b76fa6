package com.example.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow class handling the reporting of defects.
 * This is the System Under Test (SUT).
 * Note: This file represents a placeholder for the implementation logic.
 * The logic will be implemented in the Green phase, but the class exists to satisfy compilation for the test structure.
 */
public class DefectReportWorkflow {

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    public DefectReportWorkflow(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * 
     * @param title The defect title.
     * @param description The defect description.
     */
    public void reportDefect(String title, String description) {
        // TODO: Implement in Green phase
        // Expected Flow:
        // 1. gitHubPort.createIssue(title, description)
        // 2. If URL present, format message body: "New defect reported: <url>"
        // 3. slackPort.sendMessage(SLACK_CHANNEL, formattedBody)
        throw new UnsupportedOperationException("Red Phase: Implementation missing");
    }
}