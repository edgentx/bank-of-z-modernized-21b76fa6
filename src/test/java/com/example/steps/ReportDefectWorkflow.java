package com.example.steps;

import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Placeholder implementation of the Workflow logic to be tested.
 * This class represents the 'Report Defect' workflow orchestration.
 * In the actual system, this would be a Temporal Workflow or a Spring Service.
 */
public class ReportDefectWorkflow {

    private final GithubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(GithubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(String title) {
        // STUB IMPLEMENTATION FOR RED PHASE
        // Currently fails criteria because it does not include the URL in the Slack message.
        // The test SFB1E2ETest expects the URL to be present.
        
        String issueUrl = githubIssuePort.createIssue(title, "Defect details...");
        
        // BUG: The defect VW-454 states the URL is missing.
        // This code deliberately omits the URL to reproduce the defect.
        String slackMessage = "New defect reported: " + title;
        
        slackNotificationPort.postMessage(slackMessage);
    }
}