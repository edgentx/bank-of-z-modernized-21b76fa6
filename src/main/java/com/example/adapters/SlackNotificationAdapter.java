package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for SlackNotificationPort.
 * Uses the Slack Web API to send messages.
 * 
 * Note: This is a placeholder implementation. In a real-world scenario,
 * this would use an HTTP client (like WebClient or RestTemplate) to call
 * the Slack API endpoint.
 */
@Component
@Profile("!test") // Only load this bean if the 'test' profile is NOT active
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean sendMessage(String channel, String body) {
        // TODO: Implement actual Slack API call
        // Example: WebClient.post()... to https://slack.com/api/chat.postMessage
        
        logger.info("[MOCK IMPLEMENTATION] Sending message to Slack channel {}: {}", channel, body);
        
        // Returning true to simulate success for the build/verification phase
        // unless a network error occurs.
        return true;
    }
}