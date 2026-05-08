package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for sending Slack notifications.
 * In a production environment, this would use the Slack WebClient API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendDefectReport(String messageBody) {
        // Implementation stub: Real implementation would post to Slack Webhook or API.
        // Logging here to simulate external interaction.
        log.info("[SLACK] Sending notification: {}", messageBody);
        
        // Pseudocode for real implementation:
        // SlackClient.postMessage(messageBody);
    }
}
