package com.example.adapters;

import com.example.domain.defect.service.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Adapter for Slack notifications.
 * Implements the logic to format and send messages to Slack.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendNotification(String channel, String message) {
        // In a real implementation, this would use the Slack WebAPI client.
        // For the purpose of the defect fix validation, we ensure the message formatting happens here.
        System.out.println("[SLACK MOCK] Sending to channel " + channel + ": " + message);
    }
}
