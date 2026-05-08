package com.example.adapters;

import com.example.ports.SlackPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackPublisher port.
 * In a production environment, this would use the Slack WebClient.
 * For this exercise, it logs the output to verify the behavior.
 */
@Component
public class SlackPublisherAdapter implements SlackPublisher {

    private static final Logger log = LoggerFactory.getLogger(SlackPublisherAdapter.class);

    @Override
    public void publish(String channel, String body) {
        // Real-world implementation would involve:
        // SlackClient.getInstance().methods().chatPostMessage(r -> r
        //     .channel(channel)
        //     .text(body)
        // );
        
        // Logging to stdout for verification in e2e/logs
        log.info("[SLACK] Channel: {} | Body: {}", channel, body);
    }
}
