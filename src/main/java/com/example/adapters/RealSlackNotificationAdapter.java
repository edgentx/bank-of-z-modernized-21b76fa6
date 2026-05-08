package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * In a production environment, this would use the Slack WebApi to post messages.
 * Currently acts as a stub ready for actual API integration.
 */
@Component
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // In a real implementation, we would inject a SlackClient and perform:
        // slackClient.postMessage(chatPostMessage -> chatPostMessage
        //     .channel(channel)
        //     .text(body));
        
        log.info("[Real Slack Adapter] Sending to channel {}: {}", channel, body);
        // Intentionally left as a log stub for this phase, as per TDD green phase requirements.
    }
}
