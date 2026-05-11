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
 * Real-world implementation of the Slack Notification Port.
 * Uses the Slack Web API to post messages.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final String SLACK_API_URL = "https://slack.com/api/chat.postMessage";

    private final HttpClient httpClient;
    private final String botToken;

    public SlackNotificationAdapter(@Value("${slack.bot.token}") String botToken) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.botToken = botToken;
    }

    @Override
    public boolean send(String channel, String body) {
        if (botToken == null || botToken.isBlank() || botToken.equals("PLACEHOLDER")) {
            log.warn("Slack Bot Token is not configured. Skipping send.");
            return false;
        }

        try {
            // Construct JSON payload manually to avoid extra dependencies like Jackson/Gson in this specific file
            // if the project doesn't enforce them globally yet, though Spring Boot usually includes Jackson.
            // Assuming standard JSON format for Slack API.
            String jsonPayload = String.format(
                "{\"channel\": \"%s\", \"text\": \"%s\"}",
                channel,
                escapeJson(body)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SLACK_API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + botToken)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Check for "ok": true in response body (Slack API specific)
                if (response.body().contains("\"ok\":true")) {
                    return true;
                } else {
                    log.error("Slack API returned error: {}", response.body());
                    return false;
                }
            } else {
                log.error("Failed to send Slack message. HTTP status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }

        } catch (Exception e) {
            log.error("Exception while sending Slack notification", e);
            return false;
        }
    }

    /**
     * Minimal JSON escaping for the payload body.
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
