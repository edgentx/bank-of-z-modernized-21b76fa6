package com.example.adapters;

import com.example.ports.NotificationGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Production implementation of NotificationGatewayPort.
 * In a real scenario, this would use the Slack SDK or a WebClient to post messages.
 */
@Component
public class SlackNotificationGatewayAdapter implements NotificationGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationGatewayAdapter.class);

    @Override
    public void sendNotification(String channelId, String messageBody) {
        log.info("Sending notification to Slack channel {}: {}", channelId, messageBody);

        // Simulate API call logic here
        // slackClient.methods().chatPostMessage(r -> r
        //     .channel(channelId)
        //     .text(messageBody)
        // );

        log.debug("Notification sent.");
    }
}
