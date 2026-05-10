package com.example.adapters;

import com.example.domain.ports.SlackNotifier;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter for sending Slack notifications via Webhooks.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String webhookUrl;

    public SlackNotifierAdapter(
            @Value("${slack.webhook.url:https://hooks.slack.com/services/FAKE/PATH}") String webhookUrl,
            OkHttpClient httpClient
    ) {
        this.webhookUrl = webhookUrl;
        this.httpClient = httpClient;
    }

    @Override
    public void notify(String messageBody) {
        log.info("Sending Slack notification");
        
        try {
            // Construct standard Slack webhook payload
            // { "text": "message body" }
            String jsonPayload = String.format("{\"text\":\"%s\"}", escapeJson(messageBody));

            RequestBody body = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Slack notification failed: {} {}", response.code(), response.message());
                    throw new RuntimeException("Slack webhook failed with code: " + response.code());
                }
                log.info("Slack notification sent successfully");
            }
        } catch (IOException e) {
            log.error("IO Error communicating with Slack", e);
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
