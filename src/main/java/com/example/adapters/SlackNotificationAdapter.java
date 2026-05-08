package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link SlackNotificationPort} using a real Slack Web API client.
 * <p>
 * NOTE: In a staging/local environment, this implementation would likely be swapped out
 * by the mock defined in the test package via Spring Profile configuration.
 * </p>
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    // In a real-world scenario, this would use the Slack Web Client (e.g., OKHttp or WebClient)
    // private final SlackClient slackClient;

    public SlackNotificationAdapter() {
        // this.slackClient = slackClient;
    }

    @Override
    public void postMessage(String channel, String messageBody) {
        if (channel == null || messageBody == null) {
            throw new IllegalArgumentException("Channel and body must not be null");
        }
        // Implementation for real Slack API call would go here:
        // slackClient.postMessage(channel, messageBody);
        
        // For now, we log to stdout to verify execution in integration tests
        // if no external API key is configured.
        System.out.println("[SLACK ADAPTER] Posting to " + channel + ": " + messageBody);
    }
}
