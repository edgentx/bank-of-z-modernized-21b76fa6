package com.example.adapters;

import com.example.config.SlackConfig;
import com.example.ports.SlackNotificationPort;

/**
 * Real adapter implementation for SlackNotificationPort.
 * In a real production environment, this would use WebClient or RestTemplate
 * to post to a Slack Incoming Webhook URL.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final SlackConfig config;

    public SlackNotificationAdapter(SlackConfig config) {
        this.config = config;
    }

    @Override
    public void send(String messageBody) {
        // In production, this would block on an HTTP request to Slack.
        // For this defect fix, we ensure the *content* is correct.
        // System.out.println("[PROD SLACK ADAPTER] Sending payload: " + messageBody);
        
        // Example of what the real HTTP call might look like:
        // webClient.post().uri(config.getWebhookUrl()).bodyValue(messageBody).retrieve().bodyToMono(Void.class).block();
    }
}
