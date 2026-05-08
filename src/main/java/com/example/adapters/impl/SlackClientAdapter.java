package com.example.adapters.impl;

import com.example.adapters.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 */
@Component
public class SlackClientAdapter implements SlackNotificationPort {
    @Override
    public void sendNotification(String messageBody) {
        // Real Slack Webhook or API call here
        System.out.println("SLACK MESSAGE SENT: " + messageBody);
    }
}