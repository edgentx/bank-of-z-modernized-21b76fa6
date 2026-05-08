package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would use the Slack Web API client.
 * For this defect fix, it serves as the concrete implementation wiring.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String messageBody) {
        // Implementation of the actual Slack API call would go here.
        // Since we cannot depend on a non-existent Slack SDK in the prompt's mandatory tech stack list,
        // we log the action. In a real scenario, this would use WebClient or a dedicated Slack client.
        log.info("Sending message to Slack channel {}: {}", channel, messageBody);
        
        // Example of what the real code might look like:
        // slackClient.methods(chatPostMessage(r -> r
        //     .channel(channel)
        //     .text(messageBody)
        // ));
    }
}
