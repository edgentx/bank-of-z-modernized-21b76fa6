package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Real implementation of the Slack Notification Port.
 * Connects to the actual Slack API using Webhooks.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public SlackNotificationAdapter(
            RestTemplate restTemplate,
            @Value("${slack.webhook.url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String messageBody) {
        log.info("Sending notification to Slack: {}", messageBody);
        
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured. Skipping notification.");
            return;
        }

        try {
            // Construct the Slack payload JSON
            // Note: In a real production app, you might use a dedicated Slack library (like Slack SDK)
            // but RestTemplate keeps dependencies low for this demonstration.
            String payload = String.format("{\"text\": \"%s\"}", messageBody.replace("\"", "\\\""));

            restTemplate.postForEntity(
                URI.create(webhookUrl),
                payload,
                String.class
            );
            log.debug("Slack notification sent successfully.");
        } catch (Exception e) {
            // In a Temporal workflow, we might want to throw this to trigger a retry.
            // For now, we log the error to prevent workflow failure if Slack is down.
            log.error("Failed to send Slack notification", e);
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }
}
