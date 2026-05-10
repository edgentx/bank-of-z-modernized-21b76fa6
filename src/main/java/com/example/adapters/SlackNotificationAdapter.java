package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real adapter for sending notifications to Slack.
 * Connects to the configured Slack Webhook URL.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public SlackNotificationAdapter(RestTemplate restTemplate,
                                    @Value("${vforce360.slack.webhook-url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String messageBody) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured. Skipping notification.");
            return;
        }
        
        try {
            // Construct Slack payload
            SlackPayload payload = new SlackPayload(messageBody);
            restTemplate.postForObject(webhookUrl, payload, String.class);
            log.info("Successfully sent notification to Slack.");
        } catch (Exception e) {
            // We don't want a Slack failure to break the transaction, but we log it.
            log.error("Failed to send Slack notification", e);
        }
    }

    /**
     * Simple POJO for Slack JSON payload.
     */
    private record SlackPayload(String text) {}
}
