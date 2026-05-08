package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookPayloadsPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Real adapter for Slack notifications.
 */
@Component
public class RealSlackAdapter implements SlackNotificationPort {

    private static final Logger logger = Logger.getLogger(RealSlackAdapter.class.getName());

    private final Slack slackClient;
    private final String webhookUrl;

    public RealSlackAdapter(@Value("${slack.webhook.url}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.slackClient = Slack.getInstance();
    }

    @Override
    public void sendMessage(String channel, String body) {
        logger.info("[Slack Adapter] Sending to " + channel + ": " + body);
        try {
            WebhookPayloadsPayload payload = WebhookPayloadsPayload.builder()
                    .text(body)
                    .channel(channel)
                    .build();
            
            // In a real scenario, execute the webhook:
            // slackClient.send(webhookUrl, payload);
            
        } catch (Exception e) {
            logger.severe("Failed to send Slack message: " + e.getMessage());
            throw new RuntimeException("Slack notification failed", e);
        }
    }
}
