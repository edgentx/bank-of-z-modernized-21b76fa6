package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Real-world adapter implementation for Slack notifications.
 * Connects to the Slack Web API to post messages.
 * Designed for the Spring Boot environment.
 */
@Component
public class SlackAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);
    private static final String SLACK_API_URL = "https://slack.com/api/chat.postMessage";

    // In a real scenario, this token is injected from application.properties
    // using @Value("${slack.token}"). For the adapter pattern, we focus on the wiring.
    private final String authToken;

    public SlackAdapter() {
        // Default constructor. In production, use:
        // this.authToken = System.getenv("SLACK_TOKEN");
        this.authToken = "xoxb-placeholder-token"; 
    }

    /**
     * Sends a message via HTTP POST to Slack.
     */
    @Override
    public boolean sendMessage(String channel, String body) {
        try {
            // Construct JSON payload manually to avoid extra dependencies like Jackson
            // in this specific low-level adapter, though Jackson is available in the project.
            String jsonPayload = String.format(
                "{\"channel\": \"%s\", \"text\": \"%s\"}",
                channel, escapeJson(body)
            );

            URL url = new URL(SLACK_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                log.info("Successfully sent message to Slack channel {}: {}", channel, body);
                return true;
            } else {
                log.error("Failed to send message to Slack. Response code: {}", responseCode);
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending Slack notification", e);
            return false;
        }
    }

    /**
     * Helper to escape special characters in the message body for JSON.
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }
}
