package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Default implementation of the Slack Notification Port.
 * Uses OkHttp to post messages to a Slack Webhook.
 */
@Component
public class DefaultSlackAdapter implements SlackNotificationPort {

    private final OkHttpClient client;
    private final String webhookUrl;

    public DefaultSlackAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.client = new OkHttpClient();
    }

    @Override
    public void sendNotification(String message) {
        // Construct JSON payload for Slack
        // Slack webhook expects { "text": "..." }
        String jsonPayload = "{\"text\": \"" + message.replace("\"", "\\"") + "\"}";

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
                throw new RuntimeException("Failed to send Slack notification: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException sending Slack notification", e);
        }
    }
}
