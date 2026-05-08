package com.example.domain.defect.adapter.impl;

import com.example.domain.defect.port.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real-world adapter for posting notifications to Slack.
 * Uses Incoming Webhooks.
 */
@Component
@ConditionalOnProperty(name = "app.feature.slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotifierAdapter implements SlackNotifier {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotifierAdapter.class);
    private final RestClient restClient;

    public SlackNotifierAdapter(RestClient.Builder restClientBuilder, SlackProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.webhookUrl())
                .build();
        logger.info("Initialized SlackNotifierAdapter for webhook: {}...", 
                    properties.webhookUrl().substring(0, Math.min(20, properties.webhookUrl().length())));
    }

    @Override
    public void sendNotification(String body) {
        logger.debug("Posting notification to Slack. Body length: {}", body.length());

        // Slack Webhook payload format
        SlackRequest payload = new SlackRequest(body);

        try {
            // We use retrieve().toBodilessEntity() because Slack webhooks return 200 OK with an empty body
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            
            logger.info("Successfully posted notification to Slack");
        } catch (Exception e) {
            logger.error("Failed to post notification to Slack", e);
            // If Slack is down, we might not want to fail the whole transaction,
            // but for this defect validation, we propagate the error.
            throw new RuntimeException("Failed to notify Slack: " + e.getMessage(), e);
        }
    }

    private record SlackRequest(String text) {}

    public record SlackProperties(String webhookUrl) {}
}
