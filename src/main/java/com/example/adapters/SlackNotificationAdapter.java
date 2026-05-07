package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * This would use the Slack WebClient to actually post messages.
 * It is active only in non-test profiles (e.g., 'prod', 'dev').
 */
@Component
@Profile("!test") // Ensure this doesn't override the mock in tests
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // In a real scenario, we would inject SlackClient here
    // private final SlackClient slackClient;

    public SlackNotificationAdapter() {
        // slackClient = SlackClient.getInstance();
    }

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        try {
            // Pseudo-code for actual implementation:
            // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
            //     .channel(channelId)
            //     .text(messageBody)
            //     .build();
            // slackClient.chatPostMessage(request);
            
            log.info("Sending message to Slack channel {}: {}", channelId, messageBody);
            return true;
        } catch (Exception e) {
            log.error("Error sending Slack message", e);
            return false;
        }
    }
}