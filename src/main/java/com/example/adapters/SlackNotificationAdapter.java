package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Adapter for Slack Notifications.
 * <p>
 * In a real production environment, this class would contain the logic to interact
 * with the Slack Web API (e.g., using WebClient or a dedicated Slack SDK).
 * For the purpose of this defect fix validation, it acts as the implementation
 * that would replace the mock in a live environment.
 * </p>
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean send(String payload) {
        // Simulated implementation.
        // In production, this would perform: webClient.post().uri(slackWebhookUrl).body(payload).exchange();
        log.info("[Slack Adapter] Sending payload: {}", payload);
        return true;
    }
}
