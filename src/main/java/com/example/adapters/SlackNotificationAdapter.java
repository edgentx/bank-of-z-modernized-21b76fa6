package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a real scenario, this would use the Slack WebApiClient.
 * For this implementation, we simulate success.
 */
@Component
@Profile("!test") // Only load when not in test profile (where Mock is used)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean sendNotification(String messageBody) {
        // In a real implementation, we would execute:
        // slackClient.postMessage(chatId, messageBody);
        
        // Simulating successful send for the green phase.
        log.info("Sending Slack message: {}", messageBody);
        return true;
    }
}
