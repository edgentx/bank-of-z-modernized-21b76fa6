package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Real implementation of the SlackNotificationPort.
 * Connects to the external Slack Webhook API.
 */
@Component
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(RealSlackNotificationAdapter.class);
    private final HttpClient httpClient;

    // In a real scenario, inject the webhook URL via configuration
    private final String slackWebhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/services/FAKE/KEY/HERE");

    public RealSlackNotificationAdapter() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void postMessage(String channel, String messageBody) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null or blank");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("MessageBody cannot be null or blank");
        }

        // Construct Slack Payload
        // Note: We send the text. If using a webhook, the channel is often pre-configured,
        // but we include 'channel' in the payload to override if permissions allow.
        String jsonPayload = String.format(
            "{\"text\": \"%s\", \"channel\": \"%s\"}",
            escapeJson(messageBody),
            escapeJson(channel)
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(slackWebhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Slack API returned error: {} {}", response.statusCode(), response.body());
                throw new RuntimeException("Slack API call failed with status " + response.statusCode());
            }

            logger.debug("Successfully posted message to Slack channel {}", channel);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to send Slack notification", e);
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
