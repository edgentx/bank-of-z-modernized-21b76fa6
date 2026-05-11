package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Real-world adapter for posting messages to Slack.
 * This implementation uses the Slack Web API.
 * 
 * Note: In a production environment, secrets like tokens should be injected
 * securely, but for this implementation we rely on Spring configuration.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public SlackNotificationAdapter(
            @Value("${slack.webhook.url}") String webhookUrl,
            RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(String channel, String body) {
        // In a real Slack integration using Webhooks, the channel is often
        // pre-configured in the webhook settings, but we can override it if needed.
        // Here we construct the standard JSON payload.
        
        Map<String, String> payload = new HashMap<>();
        payload.put("channel", channel);
        payload.put("text", body);
        payload.put("mrkdwn", "true"); // Enable basic markdown formatting

        try {
            restTemplate.postForObject(webhookUrl, payload, String.class);
        } catch (Exception e) {
            // We log the error but don't throw to prevent temporal workflow failures
            // due to transient notification issues.
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }
}
