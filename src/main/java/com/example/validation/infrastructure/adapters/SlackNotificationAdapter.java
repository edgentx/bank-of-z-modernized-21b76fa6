package com.example.validation.infrastructure.adapters;

import com.example.validation.domain.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Adapter for Slack Notification.
 * This would typically use WebClient or RestTemplate to POST to a Slack Webhook URL.
 */
@Service
public class SlackNotificationAdapter implements SlackNotificationPort {

    // In a real scenario, this would be @Value("${slack.webhook.url}")
    private final String webhookUrl = "https://hooks.slack.com/services/FAKE/URL/VALUE";

    @Override
    public void sendMessage(String message) {
        // Simulated HTTP Call implementation
        // Real code:
        // WebClient.post().uri(webhookUrl).bodyValue(message).retrieve().bodyToMono(Void.class).block();
        
        System.out.println("[SlackAdapter] POST to " + webhookUrl + " with body: " + message);
    }
}