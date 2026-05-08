package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Real implementation of SlackNotificationPort using OkHttp.
 * Posts messages to the Slack Web API.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final String SLACK_API_URL = "https://slack.com/api/chat.postMessage";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String authToken;

    /**
     * Constructor for the real adapter.
     *
     * @param authToken The Slack Bot User OAuth Token (xoxb-...).
     *                  In production, this should be injected from environment variables.
     */
    public SlackNotificationAdapter(String authToken) {
        this.authToken = authToken;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void postMessage(String channel, String messageBody) {
        // Pre-flight validation (Port contract)
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel cannot be null or empty");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("messageBody cannot be null or empty");
        }

        try {
            SlackPayload payload = new SlackPayload(channel, messageBody);
            String jsonBody = objectMapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(SLACK_API_URL)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Slack API call failed with code: " + response.code());
                }
                
                String responseBody = response.body() != null ? response.body().string() : "";
                SlackResponse slackResponse = objectMapper.readValue(responseBody, SlackResponse.class);
                
                if (!slackResponse.ok) {
                    throw new RuntimeException("Slack API returned error: " + slackResponse.error);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to post message to Slack", e);
        }
    }

    // DTOs for Slack JSON serialization
    private record SlackPayload(
            @JsonProperty("channel") String channel,
            @JsonProperty("text") String text
    ) {}

    private record SlackResponse(
            @JsonProperty("ok") boolean ok,
            @JsonProperty("error") String error
    ) {}
}
