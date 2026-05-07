package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * Adapter for posting notifications to Slack.
 * Implements the SlackNotificationPort interface.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String slackWebhookUrl;
    private final ObjectMapper objectMapper;

    public SlackNotificationAdapter(
            @Value("${slack.webhook.url}") String slackWebhookUrl,
            ObjectMapper objectMapper) {
        this.slackWebhookUrl = slackWebhookUrl;
        this.objectMapper = objectMapper;
        // Configure client with timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30)) // Wait for Slack response
                .build();
    }

    @Override
    public void postMessage(String channelId, String message) {
        try {
            // Construct the Slack Webhook Payload
            // If using Webhooks, we often push text directly, but we can structure blocks if needed.
            // Given the requirement for a specific body, we pass the formatted message string.
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("text", message);
            // If specific channel routing is needed via webhook (rare, usually implicit), we add it.
            // payload.put("channel", channelId); 

            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url(slackWebhookUrl)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Slack notification failed with code {}: {}", response.code(), response.body().string());
                    throw new RuntimeException("Failed to send Slack notification. Code: " + response.code());
                }
                log.info("Successfully posted message to Slack channel {}", channelId);
            }
        } catch (IOException e) {
            log.error("IO Error sending Slack notification", e);
            throw new RuntimeException("Error sending Slack notification", e);
        }
    }
}
