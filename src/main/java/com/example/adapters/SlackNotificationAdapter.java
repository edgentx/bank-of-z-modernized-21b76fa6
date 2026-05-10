package com.example.adapters;

import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;\nimport org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real adapter for sending notifications to Slack.
 * Uses Spring's RestTemplate to post to a Slack Webhook.
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public SlackNotificationAdapter(@Value("${slack.webhook.url}") String webhookUrl,
                                     RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(String body) {
        log.info("Sending notification to Slack: {}", body);
        
        if (webhookUrl == null || webhookUrl.isBlank() || webhookUrl.contains("placeholder")) {
            log.warn("Slack Webhook URL is not configured. Skipping notification.");
            return;
        }

        try {
            // Construct standard Slack webhook payload
            // { "text": "<body>" }
            SlackPayload payload = new SlackPayload(body);
            restTemplate.postForObject(webhookUrl, payload, String.class);
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            // Depending on requirements, we might want to throw here.
            // For now, we log to prevent workflow failure if Slack is down.
        }
    }

    // Simple DTO for JSON serialization
    private record SlackPayload(String text) {}
}
