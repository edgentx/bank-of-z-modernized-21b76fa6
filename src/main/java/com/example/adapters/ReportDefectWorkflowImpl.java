package com.example.adapters;

import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.temporal.workflows.ReportDefectWorkflow;

/**
 * Implementation of the defect reporting workflow.
 * This logic is wrapped by the Temporal worker to execute durable workflows.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GithubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection of ports.
     * 
     * @param githubIssuePort Port for interacting with GitHub issues.
     * @param slackNotificationPort Port for sending Slack notifications.
     */
    public ReportDefectWorkflowImpl(GithubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public String report(String title) {
        // 1. Create the GitHub issue
        // Defect VW-454 specifies that the body should indicate it was reported via VForce360
        String issueUrl = githubIssuePort.createIssue(title, "Defect reported via VForce360");

        // 2. Notify Slack with the defect details and the GitHub URL
        // Defect VW-454 validation requires the Slack body to contain the GitHub URL.
        String slackMessage = String.format("Defect Created: %s\nGitHub Issue: %s", title, issueUrl);
        slackNotificationPort.sendNotification(slackMessage);

        // Return the URL for reference/tracing in the workflow history
        return issueUrl;
    }
}