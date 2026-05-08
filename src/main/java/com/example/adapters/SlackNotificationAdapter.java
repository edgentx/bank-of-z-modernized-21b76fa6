package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation for Slack notifications.
 * In a production environment, this would use the Slack WebApi to post messages.
 * For S-FB-1, this adapter performs the critical validation logic: ensuring
 * the body contains the GitHub link before posting.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Simulated external call to Slack API
        log.info("[SlackOutbound] Posting to channel {}: {}", channel, body);
    }

    @Override
    public void validateAndPost(String channel, String body) {
        // S-FB-1 Validation: Ensure body contains a GitHub URL.
        // This enforces the "Expected Behavior" described in the defect report.
        if (body == null || !body.contains("https://github.com/")) {
            throw new IllegalStateException("Validation Failed: Slack body must contain a GitHub URL.");
        }

        postMessage(channel, body);
    }
}
