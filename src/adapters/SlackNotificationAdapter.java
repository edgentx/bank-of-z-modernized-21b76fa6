package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Real-world adapter for sending notifications to Slack.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final String webhookUrl;
    private final HttpClient client;

    public SlackNotificationAdapter() {
        this.webhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/services/FAKE/PATH");
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public void sendMessage(String body) {
        // Construct Slack payload
        String slackPayload = String.format("{\"text\":\"%s\"}", escapeJson(body));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(slackPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                // Log error but don't necessarily fail the defect reporting workflow depending on requirements
                System.err.println("Failed to send Slack message: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Slack notification failed", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
