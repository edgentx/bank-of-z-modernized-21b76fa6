package com.example.adapters;

import com.example.ports.SlackClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Real-world adapter for Slack client using OkHttp.
 */
public class OkHttpSlackClient implements SlackClient {

    private static final String SLACK_WEBHOOK_URL = System.getenv("SLACK_WEBHOOK_URL");
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void sendMessage(Map<String, Object> payload) {
        try {
            String jsonBody = mapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(SLACK_WEBHOOK_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack message: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending Slack notification", e);
        }
    }
}
