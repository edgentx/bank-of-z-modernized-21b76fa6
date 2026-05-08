package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use the Slack Web API to send a message.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendDefectReport(String defectId, String message, String gitHubIssueUrl) {
        // CRITICAL: Validation Logic to satisfy VW-454
        // Ensure that the GitHub URL is actually present in the message body before sending.
        if (message == null || !message.contains(gitHubIssueUrl)) {
            throw new IllegalArgumentException(
                "Slack body validation failed for defect " + defectId + ": " +
                "The provided GitHub URL [" + gitHubIssueUrl + "] was not found in the message body."
            );
        }

        // Simulate the Slack API call
        log.info("Sending Slack notification for defect {}: {}", defectId, message);
        // WebClient.post()... implementation would go here
    }
}
