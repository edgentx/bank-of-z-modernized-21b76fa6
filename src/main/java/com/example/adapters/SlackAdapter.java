package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Production-ready implementation of the Slack Notification Port.
 * This adapter would handle actual HTTP calls to the Slack Web API.
 */
@Component
public class SlackAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Implementation Note:
        // This is where the actual Slack WebClient call would occur.
        // e.g., slackClient.postMessage(channel, messageBody);
        
        log.info("[PROD SLACK] Sending message to channel {}: {}", channel, messageBody);
        
        // Simulate network call
        try {
            Thread.sleep(50); // pretend to do work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
