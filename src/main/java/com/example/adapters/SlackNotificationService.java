package com.example.adapters;

import com.example.domain.slack.SlackMessage;
import com.example.ports.SlackNotifier;
import com.squareup.okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter for sending Slack notifications.
 */
@Component
public class SlackNotificationService implements SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String webhookUrl;

    public SlackNotificationService(@Value("${slack.webhook.url}") String webhookUrl) {
        this.client = new OkHttpClient();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void send(String message) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL not configured, skipping notification.");
            return;
        }

        // Construct standard Slack webhook payload
        String json = "{\"text\":\"" + escapeJson(message) + "\"}";

        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(RequestBody.create(json, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Slack notification failed: {}", response.body());
            } else {
                log.info("Slack notification sent successfully.");
            }
        } catch (IOException e) {
            log.error("Error sending Slack notification", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}