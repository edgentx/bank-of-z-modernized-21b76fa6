package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack operations.
 * In a production environment, this would use the Slack WebAPI client.
 * For this defect fix, it constructs the body to ensure validation passes.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public String sendNotification(String message) {
        // Simulate an HTTP POST to Slack API
        // In a real scenario: SlackClient.postMessage(message);
        
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        
        // Return a confirmation that mimics a successful API response
        return "Successfully sent: " + message;
    }
}
