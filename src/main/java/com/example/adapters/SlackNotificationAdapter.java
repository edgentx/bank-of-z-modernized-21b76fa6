package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter implementation for Slack notifications.
 * In a real scenario, this would use a WebClient or Slack Client library.
 * For defect tracking purposes, we ensure the message is logged.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {
    
    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String message) {
        // Actual implementation would POST to Slack Webhook
        log.info("Sending Slack notification: {}", message);
        // Assume success if no exception thrown
        // WebClient.post()... 
    }
}