package com.example.application;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the orchestration of defect reporting.
 * Implements the logic verified by VW454SlackBodyValidationTest.
 */
@Service
public class ValidationService {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    public ValidationService(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Executes the report defect command.
     * Orchestrates GitHub issue creation and Slack notification.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        // Step 1: Create GitHub Issue to get the tracking URL
        String issueUrl = gitHubIssuePort.createIssue(cmd.summary(), formatDetails(cmd.details()));

        // Step 2: Notify Slack, ensuring the GitHub URL is included in the body
        String slackBody = buildSlackBody(cmd, issueUrl);
        slackNotificationPort.postMessage("#vforce360-issues", slackBody);
    }

    private String buildSlackBody(ReportDefectCommand cmd, String issueUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("*New Defect Reported*\n");
        sb.append("*Severity:* ").append(cmd.severity()).append("\n");
        sb.append("*Summary:* ").append(cmd.summary()).append("\n");
        sb.append("*GitHub Issue:* ").append("<").append(issueUrl).append("|View Details>");
        return sb.toString();
    }

    private String formatDetails(java.util.Map<String, Object> details) {
        if (details == null || details.isEmpty()) {
            return "No additional details provided.";
        }
        StringBuilder sb = new StringBuilder("### Defect Details\n");
        details.forEach((k, v) -> sb.append("- **").append(k).append("**: ").append(v).append("\n"));
        return sb.toString();
    }
}
