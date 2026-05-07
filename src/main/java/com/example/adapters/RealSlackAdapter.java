package com.example.adapters;

import com.example.ports.SackPort;
import okhttp3.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Real implementation of SlackPort using OkHttp.
 * Requires a SLACK_WEBHOOK_URL environment variable.
 */
@Component
public class RealSlackAdapter implements SlackPort {

    private final OkHttpClient client = new OkHttpClient();
    private final String webhookUrl;

    public RealSlackAdapter() {
        // In a real Spring Boot app, use @Value("${slack.webhook.url}")
        this.webhookUrl = System.getenv("SLACK_WEBHOOK_URL");
        if (this.webhookUrl == null) {
            throw new IllegalStateException("SLACK_WEBHOOK_URL environment variable must be set");
        }
    }

    @Override
    public void postMessage(String channel, String body) {
        // Construct JSON payload for Slack
        // Note: channel in webhook payloads is often overridden by the webhook config,
        // but we include it here for completeness or if using a token-based API.
        String jsonPayload = "{\"text\": \"" + body.replace("\"", "\\\"") + "\"}";

        RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to post to Slack: " + response.code() + " " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException posting to Slack", e);
        }
    }
}