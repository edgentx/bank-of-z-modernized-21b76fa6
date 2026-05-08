package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real implementation for Slack notifications.
 * Uses Spring Boot's RestClient to post messages to a Slack Webhook.
 */
@Component
public class SlackWebhookAdapter implements SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackWebhookAdapter.class);

    private final RestClient restClient;
    private final String webhookUrl;

    public SlackWebhookAdapter(
            @Value("${slack.webhook.url}") String webhookUrl,
            RestClient.Builder restClientBuilder) {
        this.webhookUrl = webhookUrl;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void notify(SlackMessagePayload payload) {
        if (this.webhookUrl == null || this.webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured. Skipping notification.");
            return;
        }

        try {
            // Constructing a simple JSON payload for Slack Incoming Webhooks
            String jsonPayload = String.format(
                "{\"text\":\"%s\", \"channel\":\"%s\"}",
                escapeJson(payload.body()),
                escapeJson(payload.channel())
            );

            restClient.post()
                .uri(webhookUrl)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(jsonPayload)
                .retrieve()
                .toBodilessEntity();
            
            log.info("Successfully sent notification to Slack channel {}", payload.channel());
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            // Do not throw to prevent breaking the workflow if Slack is down
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
