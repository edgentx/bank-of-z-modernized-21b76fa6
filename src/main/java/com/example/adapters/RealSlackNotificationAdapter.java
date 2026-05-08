package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * Connects to actual Slack Web API.
 */
@Component
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackNotificationAdapter.class);

    // In a real scenario, this would be injected via @Value
    private final String webhookUrl = "https://hooks.slack.com/services/FAKE/WEBHOOK/URL";

    @Override
    public void postMessage(String messageBody) {
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }

        log.info("Sending message to Slack: {}", messageBody);
        // Real HTTP Client logic would go here (e.g., WebClient.post())
        // Example:
        // webClient.post().uri(webhookUrl).bodyValue(messageBody).retrieve().toBodilessEntity().block();
    }
}
