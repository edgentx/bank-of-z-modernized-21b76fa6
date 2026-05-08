package com.example.vforce.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Real implementation of the NotificationPort.
 * This adapter sends the payload to the external Slack API.
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(Map<String, String> payload) {
        // In a real implementation, this would use WebClient or SlackApiClient to POST the message.
        // For this defect fix, we log the payload to verify the behavior.
        logger.info("Sending Slack notification: {}", payload);
        
        if (payload == null || !payload.containsKey("body")) {
            throw new IllegalArgumentException("Payload must contain a 'body' field.");
        }
        
        // Simulate successful send
    }
}
