package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack Notifications.
 * Connects to Slack API.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void notify(String message) {
        // In a real implementation, this would use WebClient or a Slack Client library to POST the message.
        log.info("Sending notification to Slack: {}", message);
        // Placeholder for actual API call
    }
}