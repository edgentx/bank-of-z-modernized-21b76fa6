package com.example.domain.validation;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow logic for reporting defects.
 * This class represents the 'Green' phase implementation.
 * It orchestrates the notification process by constructing the message
 * and delegating transmission to the Slack port.
 */
public class DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflow.class);
    private static final String TARGET_CHANNEL = "#vforce360-issues";

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor injection for the Slack port.
     * Allows tests to inject MockSlackNotificationPort.
     */
    public DefectReportWorkflow(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * Validates input, constructs the message body, and triggers the notification.
     *
     * @param cmd The command containing defect details.
     */
    public void report(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }

        log.info("Executing defect report for ID: {}", cmd.defectId());

        // Construct the message body containing the GitHub URL
        // Format: "Slack body includes GitHub issue: <url>"
        String messageBody = buildMessageBody(cmd);

        // Send notification via port
        slackNotificationPort.sendNotification(TARGET_CHANNEL, messageBody);
    }

    private String buildMessageBody(ReportDefectCmd cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Report: ").append(cmd.title() != null ? cmd.title() : "Unknown").append("\n");
        sb.append("Description: ").append(cmd.description() != null ? cmd.description() : "No description").append("\n");
        sb.append("Severity: ").append(cmd.severity() != null ? cmd.severity() : "UNKNOWN").append("\n");

        // Critical requirement: Include the GitHub URL
        if (cmd.githubIssueUrl() != null && !cmd.githubIssueUrl().isBlank()) {
            sb.append("Slack body includes GitHub issue: ").append(cmd.githubIssueUrl());
        } else {
            sb.append("Slack body includes GitHub issue: TBD");
        }

        return sb.toString();
    }
}
