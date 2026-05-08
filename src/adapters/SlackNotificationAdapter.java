package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack Notification.
 * This is a placeholder implementation that logs the message.
 * In a production environment, this would use an HTTP client (e.g., WebClient)
 * to post to the Slack Incoming Webhook URL.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("Slack message body cannot be null");
        }
        // Simulate sending the message
        log.info("[SLACK] Sending notification: {}", messageBody);
        // Actual implementation:
        // webClient.post().uri(webhookUrl).bodyValue(messageBody).retrieve().toBodilessEntity().block();
    }
}
