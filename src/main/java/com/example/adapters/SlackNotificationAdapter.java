package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Real adapter for sending notifications to Slack.
 * Uses Java 11+ HttpClient to post messages to a Slack Webhook.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final HttpClient httpClient;
    private final String webhookUrl;

    public SlackNotificationAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void send(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }

        // In a real scenario, we might wrap the text in a JSON structure expected by Slack API.
        // For this defect validation, we are primarily concerned that the text contains the URL.
        String jsonBody = String.format("{\"text\": \"%s\"}", payload.replace("\"", "\\\""));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Slack notification failed with status {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("Failed to send Slack notification: " + response.statusCode());
            }

            log.info("Slack notification sent successfully.");
        } catch (Exception e) {
            log.error("Error sending Slack notification", e);
            throw new RuntimeException("Slack service unavailable", e);
        }
    }
}
