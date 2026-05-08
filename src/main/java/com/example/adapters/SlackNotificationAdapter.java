package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Real implementation of SlackNotificationPort.
 * In a full Spring Boot app, this would use a Slack Webclient to post the message.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // In a real implementation, inject WebClient/RestTemplate here.

    public SlackNotificationAdapter() {
    }

    @Override
    public void sendMessage(String channelId, Map<String, Object> messageBody) {
        // Mock implementation for the Adapter pattern.
        // Real implementation: webClient.post()...
        log.info("[SLACK] Sending to {}: {}", channelId, messageBody.get("text"));
    }
}