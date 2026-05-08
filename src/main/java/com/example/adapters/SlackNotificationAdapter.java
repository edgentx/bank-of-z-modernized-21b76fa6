package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real Adapter for Slack Notifications.
 * In a production environment, this would use an HTTP client (e.g., WebClient or RestTemplate)
 * to POST the message to the Slack API Webhook URL.
 * <p>
 * For the purpose of defect validation VW-454, this implementation logs the payload
 * to console to facilitate verification without requiring valid Slack credentials.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String messageBody) {
        // Log the message to verify content in e2e/regression scenarios
        log.info("Sending Slack Notification: {}", messageBody);

        // In production, uncomment below to send real request:
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(messageBody)
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}