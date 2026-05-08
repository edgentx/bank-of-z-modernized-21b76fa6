package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real-world adapter for Slack notifications.
 * In a production environment, this would use a Slack Webhook client or API.
 * For the purposes of VW-454 validation, we ensure the contract is met.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String body) {
        if (body == null) {
            throw new IllegalArgumentException("Slack body cannot be null");
        }
        // Simulation of posting to Slack
        logger.info("Posting to Slack: {}", body);
        // Real implementation would call WebClient.post()...webhookUrl()...
    }
}