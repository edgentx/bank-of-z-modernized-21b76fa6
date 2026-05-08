package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * In a production environment, this would connect to the Slack Web API.
 */
@Component
public class SlackAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    /**
     * Sends a notification to Slack.
     * Note: Currently logs the message. In production, this would perform an HTTP POST.
     */
    @Override
    public void sendNotification(String channel, String messageBody) {
        log.info("[SlackAdapter] Sending to channel {}: {}", channel, messageBody);
        
        // In a real implementation:
        // slackClient.postMessage(chatPostMessage ->
        //     chatPostMessage.channel(channel).text(messageBody)
        // );
    }
}
