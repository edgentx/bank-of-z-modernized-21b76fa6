package com.example.workflow;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;
import com.example.ports.dto.SlackMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Orchestrator logic for reporting a defect.
 * S-FB-1: Implements the logic to create a GitHub issue and notify Slack with the resulting URL.
 */
public class DefectReportWorkflow {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    // Standard channel for defect reporting according to VForce360 conventions
    private static final String DEFAULT_CHANNEL = "#vforce360-issues";

    public DefectReportWorkflow(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public CompletableFuture<Void> reportDefect(ReportDefectCmd cmd) {
        // 1. Prepare the request for GitHub
        // Title format: [Defect ID] Severity: Description snippet
        String title = String.format("[%s] %s: %s", cmd.defectId(), cmd.severity(), cmd.description());
        IssueRequest issueRequest = new IssueRequest(title, cmd.description());

        // 2. Call GitHub Adapter (Async)
        return githubPort.createIssue(issueRequest)
            .thenCompose(githubResponse -> {
                // 3. Handle the response and build the Slack message
                String slackBody = buildSlackBody(cmd, githubResponse);
                SlackMessage slackMessage = new SlackMessage(DEFAULT_CHANNEL, slackBody);

                // 4. Send Notification (Async)
                return slackPort.sendNotification(slackMessage);
            });
    }

    private String buildSlackBody(ReportDefectCmd cmd, IssueResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Defect Report Received*\n");
        sb.append("*ID:* ").append(cmd.defectId()).append("\n");
        sb.append("*Severity:* ").append(cmd.severity()).append("\n");
        sb.append("*Description:* ").append(cmd.description()).append("\n");
        
        // CRITICAL FIX for S-FB-1: Ensure URL is present
        if (response.url() != null && !response.url().isBlank()) {
            sb.append("*GitHub Issue:* ").append(response.url()).append("\n");
        } else {
            // Fallback if URL generation failed (should not happen with valid integration)
            sb.append("*GitHub Issue:* Link generation pending or failed.\n");
        }
        
        return sb.toString();
    }
}
