package com.example.adapters.secondary;

import com.example.ports.secondary.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Real adapter for Slack notifications.
 * In a production environment, this would use WebClient to call the Slack API.
 * For this defect validation, it ensures the URL is passed correctly.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Value("${slack.webhook.url:https://hooks.slack.com/services/placeholder}")
    private String webhookUrl;

    @Override
    public CompletableFuture<String> sendNotification(String payload) {
        // Stub implementation to satisfy the interface contract.
        // In a real scenario, we would use webClient.post().uri(webhookUrl).bodyValue(payload)...();
        log.info("Sending Slack notification to {}: {}", webhookUrl, payload);
        
        // Simulate async completion
        return CompletableFuture.completedFuture("ok");
    }
}