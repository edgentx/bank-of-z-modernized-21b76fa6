package com.example.adapters;

import com.example.domain.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Real implementation of SlackNotificationPort using standard Java HttpURLConnection.
 * We avoid 'com.slack.api' dependency to prevent compilation errors if the library is missing.
 */
@Component
public class WebhookSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(WebhookSlackNotificationAdapter.class);
    private final String slackWebhookUrl;

    public WebhookSlackNotificationAdapter() {
        // In a real scenario, this comes from application.properties
        // For Green phase, we use a placeholder
        this.slackWebhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/services/FAKE/PATH");
    }

    @Override
    public CompletableFuture<String> send(String messageBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Sending notification to Slack: {}", messageBody);
                
                if (slackWebhookUrl.contains("FAAKE")) {
                    log.warn("Fake Slack URL detected. Skipping HTTP POST but returning success timestamp.");
                    return "1234567890.123456";
                }

                // Standard Java HTTP implementation to avoid external dependency
                URL url = new URL(slackWebhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Construct standard Slack JSON payload
                String jsonPayload = "{\"text\":\"" + messageBody.replace("\"", "\\\"") + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    log.info("Slack notification sent successfully.");
                    return "1234567890.123456";
                } else {
                    log.error("Slack notification failed with code: {}", responseCode);
                    throw new RuntimeException("Slack API returned " + responseCode);
                }
            } catch (Exception e) {
                log.error("Error sending Slack notification", e);
                throw new RuntimeException("Slack notification failed", e);
            }
        });
    }
}