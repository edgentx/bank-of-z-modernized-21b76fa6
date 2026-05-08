package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of {@link SlackNotificationPort}.
 * In a production environment, this would use an HTTP client (e.g., WebClient)
 * to call the Slack API. For the scope of this defect fix, it demonstrates
 * the wiring required by the architecture.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Production logic would go here:
        // WebClient webClient = WebClient.create();
        // webClient.post().uri("https://slack.com/api/chat.postMessage")...
        
        log.info("[PROD MOCK] Posting to channel {}: {}", channel, body);
    }
}
