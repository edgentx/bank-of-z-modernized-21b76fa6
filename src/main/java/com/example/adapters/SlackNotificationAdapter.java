package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * This would normally use a Slack Webhook client to post messages.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendNotification(String channel, String messageBody) {
        // TODO: Implement actual Slack Webhook call.
        // System.out.println("[SLACK] Sent to " + channel + ": " + messageBody);
    }
}
