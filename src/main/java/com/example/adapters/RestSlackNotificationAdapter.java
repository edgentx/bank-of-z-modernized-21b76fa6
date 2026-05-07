package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for Slack Notification.
 * In a production environment, this would use WebClient or RestTemplate to POST
 * to the Slack API.
 */
@Component
public class RestSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RestSlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Implementation for actual Slack API call would go here.
        // e.g. webClient.post()...
        log.info("[SLACK] Posting to channel {}: {}", channel, body);
    }
}
