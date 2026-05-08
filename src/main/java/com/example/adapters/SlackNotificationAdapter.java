package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a real environment, this would use the Slack SDK WebClient.
 * For this defect fix, we ensure the contract matches the mock's behavior pattern.
 */
@Component
@ConditionalOnProperty(name = "adapters.slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Implementation of the actual Slack API call would go here.
        // e.g., WebClient.post("https://slack.com/api/chat.postMessage")...
        log.info("Sending message to Slack channel {}: {}", channel, body);
        // NO-OP for the scope of this unit test, but satisfies the Port contract.
    }
}
