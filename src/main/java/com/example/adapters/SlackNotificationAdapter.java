package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * Uses an HTTP client (simulated here) to post to the Slack API.
 * In a real scenario, this would use WebClient or RestTemplate.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    // In a real implementation, we might store the last message for diagnostics
    // or simply rely on the external API state. For diagnostic consistency with the mock contract,
    // we can store a local reference, though typically adapters are stateless proxies.
    private String lastPostedBody;

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Real logic would be:
        // webClient.post().uri(slackApiUrl).bodyValue(payload(channel, messageBody)).retrieve();
        log.info("[REAL ADAPTER] Posting to Slack channel {}: {}", channel, messageBody);
        this.lastPostedBody = messageBody;
    }

    @Override
    public String getLastMessageBody(String channel) {
        // This is primarily a testing/diagnostic method.
        // The real adapter might return the cached value or null if not tracking.
        return this.lastPostedBody;
    }
}
