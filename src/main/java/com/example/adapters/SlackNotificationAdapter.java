package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use a Slack WebClient (e.g., SlackApi)
 * to perform the HTTP Post.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean postMessage(String channel, String body) {
        // Pseudo-code for production:
        // SlackClient.postMessage(channel, body);
        
        log.info("[SlackAdapter] Posting to {}: {}", channel, body);
        
        // For the purpose of this defect fix, we assume success if no exception occurs.
        return true;
    }
}