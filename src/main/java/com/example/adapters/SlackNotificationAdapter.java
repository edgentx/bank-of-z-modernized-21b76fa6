package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Real adapter for Slack notifications.
 * Implements the Port defined in the domain layer.
 */
@Service
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String channel, String messageBody) {
        // In a real implementation, this would use the Slack WebClient or an HTTP client
        // to post the message to the webhook URL associated with the channel.
        log.info("Sending Slack notification to channel {}: {}", channel, messageBody);
        
        // Pseudo-code for actual implementation:
        // SlackClient slackClient = new SlackClient();
        // slackClient.post(channel, messageBody);
    }
}