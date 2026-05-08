package com.example.adapters;

import com.example.ports.SlackPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * Real implementation of the SlackPort using OkHttp WebClient.
 * Sends JSON payloads to the configured Slack Webhook URL.
 */
public class WebClientSlackAdapter implements SlackPort {

    private final String webhookUrl;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public WebClientSlackAdapter(String webhookUrl, OkHttpClient client, ObjectMapper mapper) {
        this.webhookUrl = webhookUrl;
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public void sendDefectNotification(String summary, String githubIssueUrl) {
        try {
            ObjectNode payload = mapper.createObjectNode();
            payload.put("text", String.format("Defect Reported: %s", summary));

            // Construct the body content. We ensure the GitHub URL is present if provided.
            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("*Summary:* ").append(summary != null ? summary : "No summary provided").append("\n");
            
            if (githubIssueUrl != null && !githubIssueUrl.isBlank()) {
                bodyBuilder.append("*GitHub Issue:* <").append(githubIssueUrl).append("|View Issue>");
            } else {
                bodyBuilder.append("*GitHub Issue:* No link provided.");
            }

            // Slack blocks formatting
            ObjectNode attachment = mapper.createObjectNode();
            attachment.put("type", "section");
            attachment.put("text", bodyBuilder.toString());
            
            payload.set("blocks", mapper.createArrayNode().add(attachment));

            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send Slack notification: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending Slack notification", e);
        }
    }
}
