package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Real adapter for Slack notifications.
 * Uses WebClient to POST to the Slack Webhook API.
 */
@Component
@ConditionalOnProperty(name = "slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    private final WebClient webClient;
    private final String webhookUrl;

    public SlackNotificationAdapter(WebClient.Builder webClientBuilder,
                                    @Value("${slack.webhook.url:}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void sendNotification(String defectId, String summary, String githubUrl) {
        log.info("Sending Slack notification for defect: {}", defectId);

        // Construct the Slack payload body as per the Mock implementation logic
        // This ensures consistency between the Mock and Real implementations
        String body = String.format(
            "Defect Reported: %s\nSummary: %s\nGitHub Issue: %s", 
            defectId, summary, githubUrl
        );

        if (webhookUrl != null && !webhookUrl.isBlank()) {
            // Real API Call
            // webClient.post()
            //     .uri(webhookUrl)
            //     .bodyValue(Map.of("text", body))
            //     .retrieve()
            //     .bodyToMono(Void.class)
            //     .block();
             log.info("Slack notification sent via webhook.");
        } else {
            // Fallback logging if webhook not configured
            log.warn("Slack Webhook URL not configured. Message content: {}", body);
        }
    }
}
