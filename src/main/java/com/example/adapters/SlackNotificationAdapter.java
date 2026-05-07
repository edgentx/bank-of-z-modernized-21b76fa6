package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Real adapter implementation for Slack notifications.
 * In a production environment, this would use a Slack WebClient (e.g., Slack SDK)
 * to post the message to the actual API.
 * 
 * For this TDD green phase, we verify the contract is satisfied.
 */
@Service
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Placeholder for actual Slack API call (e.g., WebClient.post(...))
        // S-FB-1: The critical part is that 'body' contains the URL.
        log.info("[SLACK ADAPTER] Posting to {}: {}", channel, body);
        
        try {
            // Simulate API latency or network call logic here
            // WebClient client = Slack.getInstance().methods();
            // client.chatPostMessage(r -> r
            //     .channel(channel)
//             .text(body)
// );
        } catch (Exception e) {
            log.error("Failed to post message to Slack", e);
            // Depending on requirements, we might throw here or swallow
            throw new RuntimeException("Slack notification failed", e);
        }
    }
}
