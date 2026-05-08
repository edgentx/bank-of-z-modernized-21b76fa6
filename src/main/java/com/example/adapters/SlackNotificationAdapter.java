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
import java.util.HashMap;
import java.util.Map;

/**
 * Real implementation of the Slack Notification Port.
 * Connects to the Slack Web API to post messages.
 * Implements Adapter pattern to decouple domain from infrastructure.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Value("${vforce360.slack.webhook.url}")
    private String webhookUrl;

    private final HttpClient httpClient;
    // In-memory storage for verification purposes in synchronous/test flows.
    // In a purely async distributed system, this would be handled by an outbox store.
    private final Map<String, String> lastMessages = new HashMap<>();

    public SlackNotificationAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        try {
            // Construct the JSON payload for Slack
            // Note: We explicitly map the channel in the payload even if using a webhook that might be bound to one.
            String jsonPayload = String.format(
                    "{\"channel\": \"%s\", \"text\": \"%s\", \"link_names\": true}",
                    channel,
                    escapeJson(messageBody)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            boolean success = response.statusCode() == 200;
            if (success) {
                lastMessages.put(channel, messageBody);
                logger.info("Successfully posted message to Slack channel {}", channel);
            } else {
                logger.error("Failed to post message to Slack. Status: {}, Body: {}", response.statusCode(), response.body());
            }
            return success;

        } catch (Exception e) {
            logger.error("Error sending Slack notification", e);
            return false;
        }
    }

    @Override
    public String getLastMessageBody(String channel) {
        return lastMessages.get(channel);
    }

    private String escapeJson(String text) {
        // Basic JSON escaping for the message body
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}