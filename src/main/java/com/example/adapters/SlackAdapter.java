package com.example.adapters;

import com.example.ports.NotificationPort;
import org.springframework.stereotype.Component;

/**
 * Adapter for Slack notification integration.
 * Implements the {@link NotificationPort} interface.
 */
@Component
public class SlackAdapter implements NotificationPort {

    @Override
    public void sendNotification(String defectId, String ticketUrl) {
        // Simulation of Slack API call
        // In a real scenario, this would POST to chat.postMessage
        // Body: "Defect Reported: " + defectId + "\nGitHub Issue: " + ticketUrl
        
        if (ticketUrl == null || ticketUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be null or blank when notifying Slack");
        }
    }
}
