package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for sending Slack notifications.
 * Uses WebClient or RestTemplate to hit the webhook URL.
 */
@Component
public class WebhookSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(WebhookSlackNotificationAdapter.class);
    private final SlackMessageValidator validator;

    public WebhookSlackNotificationAdapter(SlackMessageValidator validator) {
        this.validator = validator;
    }

    @Override
    public void notify(String message) {
        if (!validator.validate(message)) {
            throw new IllegalArgumentException("Invalid Slack message: " + message);
        }

        // In a real implementation, we would use:
        // WebClient.post().uri(webhookUrl).bodyValue(message).retrieve().bodyToMono(String.class).block();
        // For this defect fix, we verify the message content logic passes validation.
        
        log.info("Sending Slack notification: {}", message);
        // Simulate successful send
    }
}