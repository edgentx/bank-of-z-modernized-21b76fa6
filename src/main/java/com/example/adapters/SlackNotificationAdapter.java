package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use a Slack Web API client.
 * For this TDD Green phase, it acts as a structural placeholder satisfying the port contract.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean postMessage(String channel, String body) {
        // Real implementation would go here:
        // SlackClient.getInstance().post(channel, body);
        
        log.info("[MOCK-PROD] Posting to Slack channel {}: {}", channel, body);
        return true;
    }
}
