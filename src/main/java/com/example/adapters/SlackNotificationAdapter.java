package com.example.adapters;

import com.example.ports.SlackPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Real implementation of SlackPort using OkHttp.
 * Configurable via Spring properties.
 */
@Component
public class SlackNotificationAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String webhookUrl;

    public SlackNotificationAdapter(
            @Value("${slack.webhook-url:}") String webhookUrl) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendMessage(String message) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            logger.warn("Slack webhook URL is not configured. Message not sent: {}", message);
            // Fallback to console for visibility if not configured (common in local dev)
            return; 
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);

            String jsonPayload = mapper.writeValueAsString(payload);
            RequestBody requestBody = RequestBody.create(jsonPayload, JSON);

            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to send Slack notification: {} {}", response.code(), response.body().string());
                    throw new RuntimeException("Failed to send Slack notification: " + response.code());
                }
                logger.info("Slack notification sent successfully");
            }
        } catch (IOException e) {
            logger.error("IO Error sending Slack notification", e);
            throw new RuntimeException("Error communicating with Slack", e);
        }
    }
}
