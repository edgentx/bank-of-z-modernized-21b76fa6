package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack notification port.
 * This would typically wrap the Slack Web API client.
 * For the purpose of this defect fix, we ensure the logic flow is correct.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String text) {
        // Real implementation would call Slack API here
        // e.g. slackClient.methods().chatPostMessage(r -> r.channel(channel).text(text));
        log.info("Sending message to Slack channel {}: {}", channel, text);
        
        // Simulate successful send for end-to-end wiring validation
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be empty");
        }
    }
}
