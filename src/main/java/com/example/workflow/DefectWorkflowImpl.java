package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Implementation of the Defect Workflow.
 * Orchestrates the creation of a GitHub issue and subsequent Slack notification.
 */
public class DefectWorkflowImpl implements DefectWorkflow {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectWorkflowImpl(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    @Override
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(cmd.summary(), cmd.description(), "bug");

        // 2. Construct and Send Slack Notification
        // The Acceptance Criteria requires the body to contain "GitHub issue: <url>"
        String slackMessage = String.format(
            "Defect Reported: %s\nComponent: %s\nSeverity: %s\nGitHub issue: %s",
            cmd.summary(),
            cmd.component(),
            cmd.severity(),
            issueUrl
        );

        slackPort.sendNotification(slackMessage);
    }
}
