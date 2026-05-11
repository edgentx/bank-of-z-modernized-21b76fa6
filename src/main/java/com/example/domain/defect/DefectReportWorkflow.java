package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for reporting defects.
 * This file is generated as a STUB to satisfy the compiler in the Red Phase.
 * It intentionally fails the logic requirements of the tests (S-FB-1).
 */
public class DefectReportWorkflow {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    public DefectReportWorkflow(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title   Defect title.
     * @param body    Defect description.
     * @param channel Target Slack channel.
     */
    public void reportDefect(String title, String body, String channel) {
        // INTENTIONAL BUG (Red Phase):
        // This implementation sends a Slack message but does NOT include the GitHub URL.
        // This causes testReportDefect_shouldSendSlackNotificationContainingGitHubUrl to fail.
        
        // 1. Create GitHub Issue (We call it, but ignore the return value for the bug)
        String issueUrl = gitHubIssuePort.createIssue(title, body);
        
        // 2. Send Slack Notification
        // Bug: The body below is hardcoded and does NOT include issueUrl.
        String slackBody = "Defect Reported: " + title; 
        
        slackNotificationPort.sendMessage(channel, slackBody);
    }
}
