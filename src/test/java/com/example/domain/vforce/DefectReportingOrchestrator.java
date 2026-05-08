package com.example.domain.vforce;

import com.example.domain.vforce.port.GitHubIssuePort;
import com.example.domain.vforce.port.SlackNotificationPort;
import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.domain.vforce.model.GitHubIssue;

/**
 * Temporary Stand-in for the Temporal Workflow implementation.
 * This file represents the code that DOES NOT YET IMPLEMENT the feature correctly (or at all),
 * allowing the test to fail (Red phase).
 * 
 * In the actual repo, this would be the Workflow implementation activity/worker.
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
        // TODO: VW-454 Logic is missing here. The current implementation just sends text.
        // We intentionally leave out the URL formatting to ensure the test fails.
        
        String slackBody = "Defect Reported: " + cmd.summary(); 
        // BUG: Missing issue.getUrl() in the body string construction

        slackPort.notify(new SlackMessage(slackBody));
    }

    // Inner record for the notification payload
    public record SlackMessage(String body) {}
}
