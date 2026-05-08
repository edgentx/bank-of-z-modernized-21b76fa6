package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of SlackNotificationPort.
 * In a real environment, this would use the Slack WebClient.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String messageBody) {
        // Real implementation would use Slack API Client here.
        // For now, we log to verify execution.
        logger.info("[SLACK MOCK] Posting to {}: {}", channel, messageBody);
        
        // Example Real Implementation Pattern:
        // try {
        //     SlackClient.getInstance().postMessage(channel, messageBody);
        // } catch (SlackApiException e) {
        //     throw new RuntimeException("Failed to post Slack message", e);
        // }
    }
}
