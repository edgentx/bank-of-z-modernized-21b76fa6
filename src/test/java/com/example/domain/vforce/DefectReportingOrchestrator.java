package com.example.domain.vforce.service;

import com.example.domain.vforce.model.GitHubIssue;
import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.domain.vforce.model.SlackMessage;
import com.example.domain.vforce.port.GitHubIssuePort;
import com.example.domain.vforce.port.SlackNotificationPort;

/**
 * Orchestrator for reporting defects.
 * Corresponds to the Temporal Workflow logic.
 * This implementation satisfies the test requirements for VW-454.
 */
public class DefectReportingOrchestrator {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportingOrchestrator(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void report(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        GitHubIssue issue = gitHubPort.createIssue(cmd.summary(), cmd.description());

        // 2. Prepare Slack Message
        // FIX for VW-454: Include the GitHub URL in the body.
        // Format: Defect Reported: <Summary>
        //         GitHub Issue: <url|Link Text>
        String issueUrl = issue.url();
        String slackBody = "Defect Reported: " + cmd.summary() + "\n" +
                          "GitHub Issue: <" + issueUrl + "|View Issue>";

        slackPort.notify(new SlackMessage(slackBody));
    }
}
