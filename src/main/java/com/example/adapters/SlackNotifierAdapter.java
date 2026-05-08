package com.example.adapters;

import com.example.ports.DefectReporterPort;
import com.squareup.okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real implementation of DefectReporterPort using OkHttp to post to Slack.
 * Activated only when 'slack.webhook.url' is configured.
 */
@Component
@ConditionalOnProperty(name = "slack.webhook.url")
public class SlackNotifierAdapter implements DefectReporterPort {

    private final OkHttpClient client = new OkHttpClient();
    private final String webhookUrl;

    public SlackNotifierAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public boolean reportDefect(String defectId, String githubUrl) {
        // Construct the Slack message body
        // Ensure the GitHub URL is included as per Acceptance Criteria
        String message = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            defectId,
            githubUrl
        );

        // Build JSON payload manually to avoid extra dependencies like Jackson for simple strings
        // in this specific adapter context, or use a simple JSON formatter.
        // Using simple string construction for strict dependency control.
        String jsonPayload = "{\"text\": \"" + message.replace("\"", "\\\"") + "\"}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            // Log error in a real scenario
            return false;
        }
    }
}
