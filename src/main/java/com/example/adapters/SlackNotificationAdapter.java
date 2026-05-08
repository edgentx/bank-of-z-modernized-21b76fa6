package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use a Slack Webhook client or API.
 * For this defect fix validation, we log the output to verify the payload structure.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String messageBody) {
        // Real-world implementation would use WebClient or RestTemplate to POST to a Slack Webhook URL.
        // Example: 
        // webClient.post().uri(webhookUrl).bodyValue(messageBody).retrieve().bodyToMono(String.class).block();
        
        logger.info("[SlackAdapter] Posting message: {}", messageBody);
        
        // Simulate network latency
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}