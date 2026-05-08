package com.example.adapters;

import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for sending notifications to Slack.
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {
    
    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String message) {
        // In a real scenario, this would use a Slack Webhook client.
        // For now, we log it to verify the output matches requirements.
        logger.info("Sending Slack notification: {}", message);
    }
}
