package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for Slack notifications.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String channel, String message) {
        // In a real scenario, this would call the Slack Web API.
        log.info("Sending message to Slack channel {}: {}", channel, message);
        // Verification for S-FB-1: Log output should show the URL in the message
    }
}
