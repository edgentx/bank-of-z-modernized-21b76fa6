package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real-world implementation of the Slack notification port.
 * In a real environment, this would use the Slack WebApiClient.
 * For the purpose of this defect fix, the behavior is satisfied by the domain logic,
 * but this adapter exists to satisfy the Spring Boot architecture requirements.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean sendMessage(String channel, String body) {
        // TODO: Integrate actual Slack WebClient (e.g., slack-api-client)
        // For now, we log to simulate the send operation.
        log.info("Sending message to Slack channel {}: {}", channel, body);
        return true;
    }
}