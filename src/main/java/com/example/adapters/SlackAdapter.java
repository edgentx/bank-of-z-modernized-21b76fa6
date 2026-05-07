package com.example.adapters;

import com.example.ports.SlackPort;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

public class SlackAdapter implements SlackPort {
    private final OkHttpClient client;
    private final String webhookUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public SlackAdapter(OkHttpClient client, String webhookUrl) {
        this.client = client;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendMessage(String channel, Map<String, String> message) {
        try {
            // Slack Webhook payload structure
            Map<String, Object> payload = Map.of(
                "channel", channel,
                "text", message.get("text")
            );

            RequestBody jsonBody = RequestBody.create(
                mapper.writeValueAsString(payload),
                okhttp3.MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(webhookUrl)
                .post(jsonBody)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack message: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending Slack message", e);
        }
    }
}
