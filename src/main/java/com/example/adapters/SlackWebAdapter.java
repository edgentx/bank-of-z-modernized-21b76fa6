package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Real implementation of SlackPort using standard Java 11+ HttpClient.
 * In a real scenario, this would use the Slack SDK.
 */
@Component
public class SlackWebAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackWebAdapter.class);
    private final String webhookUrl;
    private final HttpClient httpClient;

    public SlackWebAdapter(@Value("${slack.webhook.url:https://slack.com/api/mock}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public CompletableFuture<Void> sendMessage(String channel, String messageBody) {
        // Construct JSON payload for Slack Webhook
        // { "text": "<message>", "channel": "<channel>" }
        // Note: Incoming webhooks usually target a specific channel, but we include it for logic completeness.
        String jsonPayload = String.format("{\"text\": \"%s\", \"channel\": \"%s\"}", 
                escapeJson(messageBody), channel);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        logger.info("Sending Slack message to {}: {}", channel, messageBody);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        logger.error("Slack API failed with status {}: {}", response.statusCode(), response.body());
                        // In a production system, we might throw a retryable exception here
                    } else {
                        logger.debug("Slack message sent successfully");
                    }
                    return null; // Void
                });
    }

    @Override
    public String getEndpointUrl() {
        return webhookUrl;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
