package com.example.adapters;

import com.example.ports.SlackNotifier;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * Real implementation of SlackNotifier using OkHttp.
 */
public class WebClientSlackAdapter implements SlackNotifier {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String webhookUrl;
    private final OkHttpClient client;

    public WebClientSlackAdapter(String webhookUrl, OkHttpClient client) {
        this.webhookUrl = webhookUrl;
        this.client = client;
    }

    @Override
    public void send(String body) {
        // Construct Slack JSON payload
        String jsonPayload = "{\"text\": \"" + body + "\"}";

        RequestBody reqBody = RequestBody.create(jsonPayload, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(reqBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to send Slack notification: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending Slack notification", e);
        }
    }
}
