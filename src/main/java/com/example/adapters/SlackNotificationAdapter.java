package com.example.adapters;

import com.example.domain.validation.model.SlackNotificationPostedEvent;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use the Slack Web API client.
 * For this story, it serves as the concrete implementation satisfying the adapter pattern.
 */
@Component
@ConditionalOnProperty(name = "app.adapter.slack.enabled", havingValue = "true", matchIfMissing = true)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(SlackNotificationPostedEvent event) {
        // Placeholder for actual Slack API call logic.
        // S-FB-1 primarily validates the Domain content generation (Aggregate logic),
        // but the adapter structure is required by the prompt.
        
        log.info("Sending Slack Notification to {}: {}", event.channel(), event.body());
        
        // Actual implementation would look like:
        // slackClient.methods(chatPostMessage(req -> req
        //     .channel(event.channel())
        //     .text(event.body())
        // ));
    }
}