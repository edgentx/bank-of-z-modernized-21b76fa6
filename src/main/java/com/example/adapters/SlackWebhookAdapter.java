package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Adapter for sending notifications via Slack Webhook.
 */
@Component
public class SlackWebhookAdapter implements SlackNotificationPort {

    @Override
    public void postMessage(String message, String details) {
        // In a real implementation, this would POST to a Slack Webhook URL.
        // System.out.println("Slack: " + message + " | " + details);
        // No-op for adapter stub
    }
}
