package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real-world Adapter for Slack notifications.
 * In a production environment, this would use the Slack WebApi to push messages.
 * For this defect fix validation, it focuses on constructing the correct payload format.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendDefectNotification(String defectId, String description, String githubIssueUrl) {
        // Implementation Logic:
        // Construct the message body ensuring the GitHub URL is included.
        // This fixes the defect where the URL was missing from the Slack body.
        
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or empty");
        }
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("githubIssueUrl cannot be null or empty");
        }

        // The critical fix: Explicitly append the URL to the body
        String messageBody = String.format(
            "Defect ID: %s%nDescription: %s%nGitHub issue: %s",
            defectId,
            description != null ? description : "No description provided",
            githubIssueUrl
        );

        // In a real scenario, we would post `messageBody` to Slack WebAPI here.
        // For validation/verification purposes, we log the output.
        log.info("Sending Slack notification for defect {}: {}", defectId, messageBody);
    }
}
