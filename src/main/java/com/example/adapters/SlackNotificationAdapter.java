package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Real adapter for Slack notifications using Webhooks.
 * For production, this would hit the Slack API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();

    @Value("${slack.webhook.url:https://hooks.slack.com/services/FAKE/URL/FOR_TESTING}")
    private String webhookUrl;

    @Override
    public void sendDefectNotification(String channel, String message, String githubIssueUrl) {
        logger.info("Sending Slack notification to {}: {} - Link: {}", channel, message, githubIssueUrl);

        // Construct the JSON payload for Slack
        Map<String, Object> payload = new HashMap<>();
        payload.put("channel", channel);
        payload.put("text", message + "\n" + githubIssueUrl);
        
        // In a real scenario, we might use blocks for better formatting
        // but a simple text string containing the URL satisfies the acceptance criteria.

        RequestBody body = RequestBody.create(gson.toJson(payload), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to send Slack notification: {} {}", response.code(), response.message());
                // Do not throw to prevent workflow rollback if Slack is down, but log error.
            } else {
                logger.info("Slack notification sent successfully.");
            }
        } catch (IOException e) {
            logger.error("IOException while sending Slack notification", e);
            // Swallow exception to ensure temporal workflow doesn't fail purely on slack notification
        }
    }
}
