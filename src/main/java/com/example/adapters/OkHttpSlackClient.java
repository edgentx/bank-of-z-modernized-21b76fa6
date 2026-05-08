package com.example.adapters;

import com.example.ports.SlackPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Slack Client Implementation using OkHttp.
 */
@Component
public class OkHttpSlackClient implements SlackPort {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String webhookUrl; // Injected via env or config

    public OkHttpSlackClient(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        // In a real scenario, this comes from application.properties
        this.webhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/services/FAKE/PATH");
    }

    @Override
    public void sendMessage(String channel, String text) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("channel", channel);
            payload.put("text", text);

            String jsonPayload = mapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(
                jsonPayload, 
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack message: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack", e);
        }
    }
}
