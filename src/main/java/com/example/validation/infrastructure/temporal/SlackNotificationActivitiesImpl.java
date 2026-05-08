package com.example.validation.infrastructure.temporal;

import org.springframework.stereotype.Component;

/**
 * Implementation of the Slack Notification Activity.
 * 
 * In a real-world scenario, this would inject a SlackService Port/Adapter to handle
 * the actual HTTP call to the Slack Webhook. For this TDD Green phase,
 * it simulates the logic.
 */
@Component
public class SlackNotificationActivitiesImpl implements SlackNotificationActivities {

    @Override
    public void sendSlackNotification(String title, String githubUrl, String severity) {
        // FIX: Ensure the GitHub URL is included in the message body.
        // This aligns with the Acceptance Criteria for VW-454.
        
        // Constructing the message body
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Defect Reported: ").append(title).append("\n");
        messageBuilder.append("Severity: ").append(severity).append("\n");
        
        // Critical fix: Append the GitHub URL to the body
        if (githubUrl != null && !githubUrl.isBlank()) {
            messageBuilder.append("GitHub Issue: <").append(githubUrl).append(">");
        } else {
            messageBuilder.append("GitHub Issue: PENDING");
        }

        String payload = messageBuilder.toString();
        
        // In a full implementation, we would call:
        // slackPort.sendNotification(payload);
        
        // Logging for verification during E2E run (Temporal Worker logs)
        System.out.println("[SlackActivity] Sending to Slack: " + payload);
    }
}