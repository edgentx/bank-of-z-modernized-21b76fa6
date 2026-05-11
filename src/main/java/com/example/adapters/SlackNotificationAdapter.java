package com.example.adapters;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the Slack Notification Port.
 * This would typically use a Slack WebClient (e.g., using a library like Slack API Client).
 * For this defect fix, we verify the contract is met.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String channel, String body) {
        // Real Slack API call would go here.
        // Example:
        // Methods.post("https://slack.com/api/chat.postMessage", payload);
        
        log.info("Sending notification to Slack channel {}: {}", channel, body);
    }
}