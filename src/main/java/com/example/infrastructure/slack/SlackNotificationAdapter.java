package com.example.infrastructure.slack;

import com.example.domain.vforce360.ports.VForce360NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the VForce360 Notification Port.
 * Connects to Slack (simulated).
 */
@Component
@ConditionalOnProperty(name = "app.adapter.slack.enabled", havingValue = "true", matchIfMissing = true)
public class SlackNotificationAdapter implements VForce360NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        // Simulate Slack API Call
        log.info("[SLACK] Posting message: {}", messageBody);
        // In a real implementation, this would use Slack WebApi client.
    }
}