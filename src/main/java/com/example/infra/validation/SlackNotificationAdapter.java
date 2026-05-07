package com.example.infra.validation;

import com.example.domain.validation.port.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Real adapter implementation for Slack Notification.
 * This would typically use a WebClient (Sync) or RestClient (Boot 3.2+) to call Slack API.
 */
@Service
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    // In a real scenario, inject WebClient here
    // private final WebClient webClient;

    @Override
    public void sendNotification(String messageBody) {
        log.info("Sending Slack notification: {}", messageBody);
        // Implementation note:
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(messageBody)
        //     .retrieve()
        //     .toBodilessEntity()
        //     .block(); // Synchronous block required by Domain Aggregate contract
    }
}
