package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.springframework.stereotype.Component;

/**
 * Real Adapter implementation for SlackNotifier.
 * In a production environment, this would use a WebClient to post to a Slack Webhook.
 * NOTE: This is a placeholder stub to satisfy the TDD 'Green' phase structure.
 */
@Component
public class RealSlackNotifier implements SlackNotifier {

    @Override
    public void void sendNotification(String body) {
        // IMPLEMENTATION NOTE:
        // In a full implementation, this would perform:
        // 1. HTTP POST to the configured Slack Webhook URL.
        // 2. Payload: { "text": body }
        
        // Logging the output for verification in a real deployment.
        System.out.println("[SLACK ADAPTER] Sending notification: " + body);
    }
}
