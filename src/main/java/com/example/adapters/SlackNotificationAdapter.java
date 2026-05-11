package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for Slack notifications.
 * Would integrate with Slack Web API in a production environment.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String channel, String body) {
        // In a real implementation, this would use an HTTP client to post to the Slack API.
        // e.g., webClient.post().uri("/api/chat.postMessage").body(...);
        log.info("[SLACK] Sending message to channel {}: {}", channel, body);
    }
}
