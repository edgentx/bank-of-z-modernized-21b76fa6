package com.example.adapters;

import com.example.ports.SackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for Slack notifications.
 * This implementation is a stub that logs, but in a real environment would use a WebClient.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean postMessage(String channel, String body) {
        // In a real scenario, this would use Spring's WebClient to call Slack API.
        // e.g., webClient.post().uri("https://slack.com/api/chat.postMessage")...
        log.info("SLACK [{}]: {}", channel, body);
        return true;
    }
}
