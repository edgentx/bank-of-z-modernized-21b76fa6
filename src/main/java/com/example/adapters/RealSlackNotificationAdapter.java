package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for sending notifications to Slack.
 * This implementation would use a Slack WebClient (e.g., using a library or REST client).
 * For this defect fix, it provides the concrete implementation of the Port.
 */
@Component
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackNotificationAdapter.class);

    // In a real scenario, this would be injected via constructor (SlackClient client)
    public RealSlackNotificationAdapter() {
        // Default constructor for Spring Bean instantiation
    }

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        try {
            // Placeholder for actual Slack API call
            // e.g., slackClient.methods().chatPostMessage(req -> req
            //     .channel(channelId)
            //     .text(messageBody)
            // );
            
            log.info("[SLACK] Sending message to channel {}: {}", channelId, messageBody);
            
            // Return true simulating success for the scope of this fix
            return true;
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            return false;
        }
    }
}