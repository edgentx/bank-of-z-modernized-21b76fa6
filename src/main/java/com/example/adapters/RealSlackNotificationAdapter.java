package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for sending Slack notifications.
 * In a production environment, this would use the Slack WebApi.
 */
@Component
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(RealSlackNotificationAdapter.class);

    @Override
    public void sendDefectReport(String message) {
        // Production implementation would POST to Slack Webhook
        logger.info("[SLACK] Sending notification: {}", message);
    }
}
