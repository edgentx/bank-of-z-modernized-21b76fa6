package com.example.infrastructure.adapters;

import com.example.domain.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real implementation of the SlackNotifier port.
 * Connects to Slack API to post messages.
 */
@Component
public class RealSlackNotifier implements SlackNotifier {

    private static final Logger logger = LoggerFactory.getLogger(RealSlackNotifier.class);
    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public RealSlackNotifier(@Value("${slack.webhook.url}") String webhookUrl,
                             RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(String messageBody) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            logger.warn("Slack webhook URL is not configured. Skipping notification.");
            return;
        }

        try {
            SlackMessage payload = new SlackMessage(messageBody);
            restTemplate.postForObject(webhookUrl, payload, String.class);
            logger.info("Successfully sent notification to Slack.");
        } catch (Exception e) {
            logger.error("Failed to send Slack notification", e);
            // Depending on requirements, we might throw here. 
            // For defect reporting, we usually don't want to fail the workflow if Slack is down.
        }
    }

    // Simple DTO for Slack API
    private record SlackMessage(String text) {}
}
