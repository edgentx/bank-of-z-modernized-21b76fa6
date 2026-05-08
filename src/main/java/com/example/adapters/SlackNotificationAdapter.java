package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.example.ports.dto.SlackMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

/**
 * Real implementation for Slack notifications.
 * Uses WebClient to post messages to a Slack Webhook or API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final WebClient webClient;
    private static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/MOCK/WEBHOOK/URL";

    public SlackNotificationAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl(SLACK_WEBHOOK_URL)
            .build();
    }

    @Override
    public CompletableFuture<Void> sendNotification(SlackMessage message) {
        // Map internal SlackMessage to the specific Slack Webhook JSON structure
        SlackPayload payload = new SlackPayload(message.channel(), message.body());

        return webClient.post()
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(Void.class)
            .toFuture();
    }

    private record SlackPayload(String channel, String text) {}
}
