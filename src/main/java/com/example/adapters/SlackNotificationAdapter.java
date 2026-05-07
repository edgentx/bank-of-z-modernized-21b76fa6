package com.example.adapters;

import com.example.model.SlackNotificationWithUrlCmd;
import com.example.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Real implementation of SlackNotificationPort using OkHttp.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String webhookUrl;

    public SlackNotificationAdapter(
            OkHttpClient client,
            ObjectMapper objectMapper,
            @Value("${vforce360.slack.webhook.url}") String webhookUrl) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public CompletableFuture<String> publishDefect(Command command) {
        if (!(command instanceof SlackNotificationWithUrlCmd cmd)) {
            return CompletableFuture.failedFuture(new UnknownCommandException(command));
        }

        // Construct the Slack message payload including the GitHub URL
        String messageJson = buildPayload(cmd);

        RequestBody body = RequestBody.create(messageJson, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        CompletableFuture<String> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Slack notification failed", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (response) {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new IOException("Unexpected code " + response));
                    } else {
                        // Slack Webhooks usually return 'ok'
                        future.complete(response.body().string());
                    }
                }
            }
        });

        return future;
    }

    private String buildPayload(SlackNotificationWithUrlCmd cmd) {
        try {
            // We build a simple JSON structure. 
            // In a real app, we might use a dedicated Slack DTO library.
            // This ensures the "URL in body" requirement is met.
            StringBuilder text = new StringBuilder();
            text.append("*Defect Reported: ").append(cmd.defectId()).append("*\n");
            text.append("Severity: ").append(cmd.severity()).append("\n");
            text.append("GitHub Issue: ").append(cmd.githubIssueUrl()).append("\n");

            SlackPayload payload = new SlackPayload(text.toString());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Failed to build Slack payload", e);
            return "{\"text\": \"Error building payload\"}";
        }
    }

    // DTO for Jackson serialization
    private record SlackPayload(String text) {}
}
