package com.example.adapters.impl;

import com.example.ports.SlackNotifierPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of SlackNotifierPort.
 * Would use Slack Web API to send messages.
 * Currently marked as conditional based on a property to allow mock usage in tests.
 */
@Component
@ConditionalOnProperty(name = "slack.notifier.impl", havingValue = "real", matchIfMissing = false)
public class RealSlackNotifier implements SlackNotifierPort {

    private static final Logger logger = LoggerFactory.getLogger(RealSlackNotifier.class);

    @Override
    public void sendNotification(String message) {
        // In a real implementation, this would use an HTTP client (e.g., WebClient or RestTemplate)
        // to POST to the Slack API webhook URL.
        logger.info("Sending notification to Slack: {}", message);
        
        // Placeholder for actual HTTP call
        // webClient.post().uri(slackWebhookUrl).bodyValue(message).retrieve();
    }
}
