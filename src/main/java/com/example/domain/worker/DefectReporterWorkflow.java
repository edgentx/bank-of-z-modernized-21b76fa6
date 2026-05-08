package com.example.domain.worker;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Workflow implementation for reporting defects.
 * Orchestrates the creation of an issue in the tracking system (VForce360)
 * and subsequent notification via Slack.
 */
public class DefectReporterWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReporterWorkflow.class);
    private final VForce360Port vForce360Port;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * Supports adapters for VForce360 and Slack integrations.
     */
    public DefectReporterWorkflow(VForce360Port vForce360Port, SlackNotificationPort slackNotificationPort) {
        this.vForce360Port = vForce360Port;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting saga.
     * 1. Reports defect to VForce360 (GitHub/Jira).
     * 2. Retrieves the issue URL.
     * 3. Sends a formatted notification to Slack including the URL.
     */
    public void reportDefect(String projectId, String title, String description, String severity) {
        log.info("Reporting defect {}: {}", projectId, title);

        // 1. Report to VForce360
        Map<String, String> response = vForce360Port.reportDefect(projectId, title, description, severity);
        String issueUrl = response.get("url");

        if (issueUrl == null || issueUrl.isBlank()) {
            log.error("Failed to retrieve issue URL from VForce360 for defect {}", title);
            // Depending on requirements, we might throw here, but for workflow resilience we might notify failure.
            throw new IllegalStateException("VForce360 did not return a valid issue URL");
        }

        log.info("Defect created at URL: {}", issueUrl);

        // 2. Construct Slack Message Body
        // Requirement: Slack body includes GitHub issue: <url>
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            title,
            severity,
            issueUrl
        );

        // 3. Send Notification
        // Target channel is assumed to be configured or derived; using default from defect report context.
        // For this workflow, we default to the channel specified in the defect report context or a default.
        String channel = "#vforce360-issues";
        slackNotificationPort.sendMessage(channel, slackBody);

        log.info("Notification sent to {} for defect {}", channel, title);
    }
}
