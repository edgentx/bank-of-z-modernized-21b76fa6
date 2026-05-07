package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String webhookUrl;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public SlackNotificationAdapter(OkHttpClient client,
                                    ObjectMapper objectMapper,
                                    @Value("${slack.webhook.url}") String webhookUrl) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String messageBody) {
        try {
            Map<String, String> payload = Map.of("text", messageBody);
            String jsonBody = objectMapper.writeValueAsString(payload);

            Request request = new Request.Builder()
                .url(webhookUrl)
                .post(RequestBody.create(jsonBody, JSON))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack notification: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack API", e);
        }
    }
}
