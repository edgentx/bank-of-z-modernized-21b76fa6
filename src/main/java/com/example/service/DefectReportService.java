package com.example.service;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic for reporting defects.
 * Coordinates the generation of the Slack message body and dispatching it via the port.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the VForce360 Slack channel.
     * Formats the URL using Slack's mrkdown/unfurl syntax <URL|text> to ensure
     * the link is clickable and valid (VW-454).
     *
     * @param issueId  The ID of the issue (e.g., VW-454).
     * @param issueUrl The full GitHub URL to the issue.
     */
    public void reportDefect(String issueId, String issueUrl) {
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalArgumentException("Issue URL cannot be null or empty");
        }
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("Issue ID cannot be null or empty");
        }

        // Format: <|Issue ID|>
        // Using Slack's <URL|Text> format ensures the URL is unfurled correctly.
        String formattedBody = "Defect Reported: <" + issueUrl + "|" + issueId + ">";

        slackNotificationPort.send(formattedBody);
    }
}