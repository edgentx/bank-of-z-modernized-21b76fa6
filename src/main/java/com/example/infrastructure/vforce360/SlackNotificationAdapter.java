package com.example.infrastructure.vforce360;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.ports.VForce360NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the VForce360NotificationPort.
 * Formats the Slack message body and posts it (simulated).
 */
@Component
public class SlackNotificationAdapter implements VForce360NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postDefectNotification(DefectReportedEvent event) {
        String messageBody = formatMessage(event);
        
        // In a real scenario, this would use an HTTP client (e.g., SlackClient) to post the message.
        // For this defect fix/validation, we focus on the body generation logic.
        log.info("Posting to Slack: {}", messageBody);
        
        // Simulating the external call
        // httpClient.post(event.webhookUrl(), messageBody);
    }

    /**
     * Formats the defect report into a Slack message string.
     * FIX for VW-454: Ensures the GitHub Issue URL is included.
     * 
     * @param event The defect event.
     * @return Formatted string for Slack body.
     */
    private String formatMessage(DefectReportedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Reported: ").append(event.title() != null ? event.title() : "Unknown").append("\n");
        sb.append("ID: ").append(event.defectId()).append("\n");
        sb.append("Description: ").append(event.description() != null ? event.description() : "No description").append("\n");
        
        // The Fix: Explicitly append the GitHub URL
        if (event.githubIssueUrl() != null && !event.githubIssueUrl().isBlank()) {
            sb.append("GitHub Issue: <").append(event.githubIssueUrl()).append(">\n");
        }
        
        return sb.toString();
    }
}
