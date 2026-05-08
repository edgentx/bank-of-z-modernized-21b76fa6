package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for Slack interactions.
 * Encapsulates the logic to post messages to Slack channels.
 * In a live environment, this would use the Slack SDK or a WebClient to hit the Webhook or Chat API.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    public SlackAdapter() {
        // Default constructor for Spring
    }

    /**
     * Posts a message to the specified channel.
     * Logs the interaction to simulate external API call.
     */
    @Override
    public void postMessage(String channelId, String text) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("Slack Channel ID cannot be null or empty");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Slack message text cannot be null or empty");
        }

        log.info("Posting message to Slack Channel [{}]: {}", channelId, text.replace("\n", " | "));
        
        // In a real implementation, we would execute:
        // slackClient.methods(chatPostMessage -> chatPostMessage
        //     .channel(channelId)
        //     .text(text)
        // );
    }
}
