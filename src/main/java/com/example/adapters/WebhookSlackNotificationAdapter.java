package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

/**
 * Adapter responsible for sending notifications to Slack.
 * This adapter enforces validation logic before sending.
 */
@Component
public class WebhookSlackNotificationAdapter {

    private final SlackMessageValidator validator;

    public WebhookSlackNotificationAdapter(SlackMessageValidator validator) {
        this.validator = validator;
    }

    /**
     * Posts a message to the configured Slack webhook.
     * @param body The message body.
     * @throws IllegalArgumentException if validation fails.
     */
    public void post(String body) {
        if (!validator.containsGitHubUrl(body)) {
            throw new IllegalArgumentException("Slack body validation failed: GitHub URL missing");
        }
        // Actual Slack HTTP call would go here
    }
}
