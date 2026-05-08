package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use the Slack Web SDK.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channelId, String messageBody) {
        // Placeholder for production logic using Slack SDK Client.
        // e.g., MethodsClient.chatPostMessage(r -> r.channel(channelId).text(messageBody));
        log.info("[SLACK ADAPTER] Sending to channel {}: {}", channelId, messageBody);
    }
}
