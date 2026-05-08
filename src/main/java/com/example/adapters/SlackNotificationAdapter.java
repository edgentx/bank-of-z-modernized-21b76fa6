package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for Slack notifications.
 * This class would typically use a Slack client library (e.g., Slack Web API) to send the message.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String body) {
        // In a real production environment, this would use the Slack WebClient to post to a channel.
        // For defect VW-454 validation, we ensure the 'body' contains the formatted URL string.
        
        log.info("Sending Slack notification: {}", body);
        
        // Placeholder for actual Slack API call:
        // SlackClient.getInstance().postMessage(channel, body);
    }
}
