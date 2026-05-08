package com.example.adapters;

import com.example.ports.SlackNotifier;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Real implementation of SlackNotifier using OkHttp.
 * This is the actual adapter that would hit the Slack API in a non-test environment.
 */
@Component
public class SlackWebHookAdapter implements SlackNotifier {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void sendNotification(String webhookUrl, String text, Map<String, Object> attachments) {
        // Construct a basic Slack message payload
        // In a real scenario, we might serialize the 'attachments' map more thoroughly
        String jsonPayload = "{\"text\": \"" + text.replace("\"", "\\\"") + "\"}";

        RequestBody body = RequestBody.create(jsonPayload, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected code " + response);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }
}