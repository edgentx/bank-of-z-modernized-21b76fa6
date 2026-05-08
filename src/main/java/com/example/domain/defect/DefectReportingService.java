package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Domain Service for reporting defects.
 * Orchestrates the creation of the defect report and notification via Slack.
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * Constructs the Slack payload ensuring the GitHub URL is present
     * and delegates sending to the port.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        log.info("Processing defect report: {}", cmd.defectId());

        // Construct the payload for Slack.
        // Format requirement: "GitHub issue: <url>"
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("Defect Reported: ").append(cmd.title() != null ? cmd.title() : "Unknown").append("\n");
        payloadBuilder.append("Description: ").append(cmd.description() != null ? cmd.description() : "N/A").append("\n");
        payloadBuilder.append("Severity: ").append(cmd.metadata() != null ? cmd.metadata().getOrDefault("severity", "UNDEFINED") : "UNDEFINED").append("\n");
        
        // Critical validation fix for S-FB-1: Ensure URL is appended
        if (cmd.githubUrl() != null && !cmd.githubUrl().isBlank()) {
            payloadBuilder.append("GitHub issue: ").append(cmd.githubUrl());
        } else {
            throw new IllegalArgumentException("GitHub URL is mandatory for defect reporting");
        }

        String payload = payloadBuilder.toString();
        slackNotificationPort.send(payload);
    }
}
