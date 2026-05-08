package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Real adapter for posting messages to Slack using the Web API.
 * This adapter is active when 'slack.webhook.url' is configured.
 */
@Component
@ConditionalOnProperty(name = "slack.webhook.url")
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String webhookUrl;
    private final ObjectMapper mapper;

    // Record for JSON serialization matching Slack API expectations
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SlackPayload(
        @JsonProperty("text") String text,
        @JsonProperty("channel") String channel
    ) {}

    public SlackNotificationAdapter(
            @Value("${slack.webhook.url}") String webhookUrl,
            @Value("${slack.timeout.seconds:5}") int timeoutSeconds
    ) {
        this.webhookUrl = webhookUrl;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public boolean postMessage(String channel, String messageBody) {
        try {
            SlackPayload payload = new SlackPayload(messageBody, channel);
            String jsonBody = mapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    logger.info("Successfully posted message to Slack channel {}", channel);
                    return true;
                } else {
                    logger.error("Failed to post message to Slack. Code: {}, Message: {}", response.code(), response.message());
                    return false;
                }
            }
        } catch (IOException e) {
            logger.error("IOException while posting to Slack: {}", e.getMessage(), e);
            return false;
        }
    }
}
