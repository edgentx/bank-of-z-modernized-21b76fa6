package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter implementation for Slack notifications using Webhooks.
 */
@Component
public class SlackWebHookAdapter implements SlackNotifierPort {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String webhookUrl;

    public SlackWebHookAdapter(
            @Value("${slack.webhook.url}") String webhookUrl,
            OkHttpClient httpClient,
            ObjectMapper objectMapper) {
        this.webhookUrl = webhookUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(String channel, String messageBody) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalStateException("Slack Webhook URL is not configured.");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null or empty.");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be null or empty.");
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(
                    new SlackPayload(channel, messageBody)
            );

            RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack message. Code: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack API", e);
        }
    }

    private record SlackPayload(String channel, String text) {}
}
