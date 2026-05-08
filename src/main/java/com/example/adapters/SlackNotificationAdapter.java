package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a real scenario, this would use a Slack Web API client.
 * For this defect fix, we simply log the message as we are validating the data flow.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channelId, String messageBody) {
        // Actual HTTP call to Slack Web API would go here
        // e.g., slackClient.methods().chatPostMessage(req -> req
        //     .channel(channelId)
        //     .text(messageBody)
        // );
        log.info("[Slack Mock] Sending to {}: {}", channelId, messageBody);
    }
}
