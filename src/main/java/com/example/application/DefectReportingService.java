package com.example.application;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the logic for reporting defects.
 * This orchestrates the creation of the notification message based on domain events
 * and dispatches it via the Slack port.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor injection for the Slack port.
     * @param slackNotificationPort The adapter capable of sending Slack messages.
     */
    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the DefectReportedEvent by formatting the message and sending it to Slack.
     * 
     * FIX for VW-454: Ensure the GitHub Issue URL is included in the message body.
     * 
     * @param event The domain event containing defect details.
     * @param targetChannel The Slack channel to notify.
     */
    public void reportDefect(DefectReportedEvent event, String targetChannel) {
        if (event == null) {
            throw new IllegalArgumentException("DefectReportedEvent cannot be null");
        }
        if (targetChannel == null || targetChannel.isBlank()) {
            throw new IllegalArgumentException("Target channel cannot be blank");
        }

        // Format the message body ensuring all fields from the event are present
        // Specifically addressing VW-454 by appending the URL.
        String messageBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            event.title(),
            event.severity(),
            event.githubIssueUrl()
        );

        slackNotificationPort.send(targetChannel, messageBody);
    }
}
