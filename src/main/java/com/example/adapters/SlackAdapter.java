package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * Real implementation for posting notifications to Slack.
 */
@Component
public class SlackAdapter implements SlackNotificationPort {

    private final OkHttpClient httpClient;
    private final String webhookUrl;

    public SlackAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public void postMessage(String channel, String messageBody) {
        // Construct the payload expected by Slack Incoming Webhooks
        String jsonPayload = String.format(
                "{\"channel\": \"%s\", \"text\": \"%s\", \"link_names\": true}",
                channel, escapeJson(messageBody)
        );

        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(RequestBody.create(jsonPayload, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to post message to Slack: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}