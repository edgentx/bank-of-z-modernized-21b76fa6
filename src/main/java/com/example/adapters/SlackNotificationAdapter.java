package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack notifications.
 * Implements the SlackNotificationPort interface.
 * 
 * This is a concrete implementation that would typically use the Slack WebClient.
 * For defect S-FB-1, this ensures the infrastructure layer follows the Adapter pattern.
 */
@Component
@ConditionalOnProperty(name = "slack.adapter.enabled", havingValue = "real", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // In a real scenario, this would be autowired:
    // private final SlackClient slackClient;

    public SlackNotificationAdapter() {
        // Initialization logic
    }

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Real implementation logic goes here (e.g., slackClient.post(channel, messageBody))
        log.info("[REAL ADAPTER] Sending message to Slack channel {}: {}", channel, messageBody);
        
        // Implementation Note:
        // The core logic for URL generation is in VForce360Service. 
        // This adapter is purely for transport.
        try {
            // Simulate network call
            // slackClient.postMessage(ChatPostMessage.builder()
            //    .channel(channel)
            //    .text(messageBody)
            //    .build());
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            throw new RuntimeException("Slack notification failed", e);
        }
    }
}
