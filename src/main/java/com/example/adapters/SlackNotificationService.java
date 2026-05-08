package com.example.adapters;

import com.example.ports.SlackPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Adapter for Slack notifications.
 * This implementation uses OkHttp to send messages to a Webhook.
 * NOTE: Previous compilation errors were due to missing OkHttp dependency.
 */
@Service
public class SlackNotificationService implements SlackPort {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String webhookUrl;

    public SlackNotificationService() {
        this.client = new OkHttpClient();
        this.webhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/dummy");
    }

    @Override
    public void sendNotification(String message) {
        // Construct the JSON payload expected by Slack Incoming Webhooks
        // We wrap the text in the "text" field of the root object.
        String json = "{\"text\": \"" + message + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected code " + response);
            }
        } catch (IOException e) {
            // In a production system, we might want to throw a custom exception
            // or retry the logic. For now, we wrap it in a RuntimeException.
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }
}