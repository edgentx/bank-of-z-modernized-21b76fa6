package com.example.workflow;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

import java.net.URI;

/**
 * Implementation of the ReportDefectWorkflow.
 * This class acts as the orchestrator, handling the logic of creating an issue
 * and then notifying Slack, ensuring the URL is passed correctly.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public ReportDefectWorkflowImpl(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public void execute(String defectId, String message) {
        // 1. Create GitHub Issue
        URI issueUrl = gitHubPort.createIssue(defectId, message);

        // 2. Notify Slack with the Issue URL
        // This is the fix for VW-454: ensure the URL is passed to the notification.
        slackPort.sendDefectNotification(defectId, message, issueUrl);
    }
}
