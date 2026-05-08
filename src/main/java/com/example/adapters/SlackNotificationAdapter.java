package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real-world adapter for posting messages to Slack.
 * In a production environment, this would use the Slack Web API.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Implementation for real Slack connection would go here.
        // For the purpose of this defect fix and build stability, we log.
        log.info("[Slack Mock] Sending to {}: {}", channel, messageBody);
        
        // Example: WebClient.post()...api.slack.com/chat.postMessage...
    }
}
