package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to handle the E2E flow of reporting defects.
 * Implements S-FB-1: Validating VW-454 GitHub URL in Slack body.
 */
public class ValidationService {

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;

    public ValidationService(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public void handleReportDefect(Command cmd) {
        if (!(cmd instanceof ReportDefectCmd reportDefectCmd)) {
            throw new IllegalArgumentException("Unknown command type: " + cmd.getClass().getSimpleName());
        }

        // 1. Create GitHub Issue
        // Extracting title and constructing description from command data
        String title = reportDefectCmd.title();
        String description = buildDescription(reportDefectCmd);
        
        String url = gitHubPort.createIssue(title, description);
        
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("GitHub Port returned a blank URL");
        }

        // 2. Prepare Slack Payload
        Map<String, String> attachments = new HashMap<>();
        // CRITICAL for S-FB-1: Ensure the github_url is populated
        attachments.put("github_url", url);
        attachments.put("defect_id", reportDefectCmd.defectId());
        
        // 3. Send Slack Notification
        // The body includes the Defect ID to satisfy E2E validation
        String body = String.format("Defect Reported: %s", reportDefectCmd.defectId());
        slackPort.sendNotification(body, attachments);
    }

    private String buildDescription(ReportDefectCmd cmd) {
        StringBuilder sb = new StringBuilder(cmd.title());
        if (cmd.metadata() != null && !cmd.metadata().isEmpty()) {
            sb.append("\n\nMetadata:\n");
            cmd.metadata().forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        }
        return sb.toString();
    }
}