package com.example.services;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service handling the reporting of defects.
 * Orchestrates the validation of the defect command and the notification
 * to external systems like Slack.
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Processes a ReportDefectCmd.
     * Validates the command content (specifically the GitHub URL) and
     * sends a notification to the configured Slack channel.
     *
     * @param cmd The command containing defect details.
     * @throws IllegalArgumentException if the GitHub URL is missing or invalid.
     */
    public void processReport(ReportDefectCmd cmd) {
        // Validate Input (VW-454 requirement)
        if (cmd.githubIssueUrl() == null || cmd.githubIssueUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub URL is required to report a defect.");
        }

        // Construct the Slack Message
        // The critical requirement is that the body MUST contain the GitHub URL.
        String slackChannel = "#vforce360-issues";
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Reported: *").append(escapeSlack(cmd.title())).append("\n");
        bodyBuilder.append("*Severity: *").append(escapeSlack(cmd.severity())).append("\n");
        bodyBuilder.append("*Component: *").append(escapeSlack(cmd.component())).append("\n");
        bodyBuilder.append("*Project ID: *").append(cmd.projectId()).append("\n");
        bodyBuilder.append("\n");
        
        // CRITICAL: Include the GitHub URL in the body
        bodyBuilder.append("GitHub Issue: ").append(cmd.githubIssueUrl()).append("\n");
        
        if (cmd.description() != null && !cmd.description().isBlank()) {
            bodyBuilder.append("\n*Description: *\n").append(escapeSlack(cmd.description()));
        }

        String body = bodyBuilder.toString();

        // Send Notification
        boolean sent = slackNotificationPort.send(slackChannel, body);
        
        if (!sent) {
            log.error("Failed to send defect notification to Slack channel {} for defect {}", slackChannel, cmd.defectId());
            // Depending on business requirements, we might want to throw here.
            // For now, we log the failure as the adapter returned false.
        } else {
            log.info("Successfully reported defect {} to Slack {}", cmd.defectId(), slackChannel);
        }
    }

    /**
     * Basic helper to escape Slack special characters to prevent formatting breaks.
     * (Minimal implementation for MVP).
     */
    private String escapeSlack(String input) {
        if (input == null) return "";
        // Slack mrkdwn special chars: & < > _ ~ * ` [ ] ( )
        // For this specific fix, we ensure the URL is passed through, but let's sanitize the rest lightly.
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
