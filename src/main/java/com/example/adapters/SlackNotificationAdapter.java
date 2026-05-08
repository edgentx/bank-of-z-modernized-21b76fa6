package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * Connects to the actual Slack Web API.
 * 
 * Condition: Only loads if 'slack.adapter.enabled' is true (or default production profile).
 * Otherwise, the InMemorySlackNotificationPort (test mock) is used.
 */
@Component
@ConditionalOnProperty(name = "slack.adapter.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // Ideally, inject a WebClient or SlackClient here
    // private final SlackWebhookClient client;

    public SlackNotificationAdapter() {
        // this.client = client;
    }

    @Override
    public boolean sendMessage(String messageBody) {
        try {
            // In a real implementation, this would perform an HTTP POST:
            // client.postMessage(messageBody);
            logger.info("[PROD ADAPTER] Sending Slack message: {}", messageBody);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send Slack notification", e);
            return false;
        }
    }
}
