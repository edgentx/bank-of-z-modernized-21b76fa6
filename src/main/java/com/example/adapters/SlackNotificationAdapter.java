package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real adapter for Slack notification.
 * Connects to Slack Web API.
 */
@Component
@ConditionalOnProperty(name = "slack.adapter.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final RestClient restClient;
    private final String token;

    public SlackNotificationAdapter(RestClient.Builder restClientBuilder,
                                     @Value("${slack.api.url}") String apiUrl,
                                     @Value("${slack.api.token}") String token) {
        this.token = token;
        this.restClient = restClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public boolean postMessage(String channel, String messageBody) {
        try {
            // Construct Slack chat.postMessage payload
            SlackRequest payload = new SlackRequest(channel, messageBody);
            
            // Execute POST (Blocking call for simplicity in this scope)
            restClient.post()
                    .uri("/api/chat.postMessage")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to post message to Slack channel {}: {}", channel, e.getMessage());
            return false;
        }
    }

    private record SlackRequest(String channel, String text) {}
}
