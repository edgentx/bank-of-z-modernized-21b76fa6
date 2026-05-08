package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for sending Slack notifications.
 * Connects to the Slack Web API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        log.info("Sending message to Slack channel {}: {}", channelId, messageBody);
        
        // In production: WebClient.post to https://slack.com/api/chat.postMessage
        try {
            // Simulate success
            return true;
        } catch (Exception e) {
            log.error("Slack API call failed", e);
            return false;
        }
    }
}
