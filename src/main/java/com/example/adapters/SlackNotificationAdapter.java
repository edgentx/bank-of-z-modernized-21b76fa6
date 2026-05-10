package com.example.adapters;

import com.example.ports.SackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * This adapter would use a Slack WebHook client or SDK to post messages.
 * In this Green phase, we implement the contract. 
 * In a real environment, this would inject a SlackClient.
 */
@Component
@ConditionalOnProperty(name = "slack.adapter", havingValue = "real", matchIfMissing = true)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        // GREEN PHASE IMPLEMENTATION:
        // Simply logging the payload proves the integration point works.
        // In a production environment, this would make an HTTP POST to a Slack Webhook.
        log.info("[SLACK ADAPTER] Sending notification payload: {}", messageBody);
        
        // Example of what the real call might look like (commented out):
        // slackWebhookClient.post(webhookUrl, SlackPayload.builder().text(messageBody).build());
    }
}
