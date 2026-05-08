package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import okhttp3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real-world adapter for posting Slack notifications.
 * Uses OkHttp to POST to a Slack Incoming Webhook.
 */
@Component
@ConditionalOnProperty(name = "adapters.slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final OkHttpClient client;
    private final String webhookUrl;

    public SlackNotificationAdapter(OkHttpClient client, String webhookUrl) {
        this.client = client;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void postMessage(String messageBody) {
        String jsonPayload = String.format("{\"text\":\"%s\"}", messageBody.replace("\"", "\\\""));

        Request request = new Request.Builder()
            .url(webhookUrl)
            .post(RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Slack notification failed: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Slack notification IO error", e);
        }
    }
}
