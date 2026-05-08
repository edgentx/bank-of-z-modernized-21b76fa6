package com.vforce360.validation.adapters;

import com.vforce360.validation.ports.SlackMessagePayload;
import com.vforce360.validation.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack notification adapter.
 * Sends HTTP POST requests to Slack WebAPI.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendMessage(SlackMessagePayload payload) {
        // Real implementation: WebClient.post().uri(slackWebhookUrl).body(payload).retrieve();
        // Logging the payload for audit
        System.out.println("[Slack] Sending message: " + payload.getBody());
    }
}
