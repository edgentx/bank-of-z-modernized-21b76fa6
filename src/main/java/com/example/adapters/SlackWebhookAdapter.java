package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.springframework.beans.factory.annotation.Value;\nimport org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real implementation for Slack notifications via Webhook.
 * Wired in production, replaced by MockSlackNotifier in tests.
 */
@Component
public class SlackWebhookAdapter implements SlackNotifier {

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public SlackWebhookAdapter(@Value("${slack.webhook.url}") String webhookUrl,
                               RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(String message) {
        // In a real scenario, we would construct a JSON payload matching Slack's API expectations.
        // For this defect fix validation, we perform the HTTP POST.
        // The exact JSON format depends on Slack App configuration (Incoming Webhooks).
        
        if (webhookUrl == null || webhookUrl.isBlank()) {
            // If running in a profile without a webhook configured, log to stdout for verification
            System.out.println("[SLACK WEBHOOK SIMULATION] " + message);
            return;
        }

        try {
            // Simple text payload
            var payload = new SlackPayload(message);
            restTemplate.postForObject(webhookUrl, payload, String.class);
        } catch (Exception e) {
            // Swallow exceptions in tests to avoid noise, but log in real app
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }

    // DTO for Slack API
    private record SlackPayload(String text) {}
}
