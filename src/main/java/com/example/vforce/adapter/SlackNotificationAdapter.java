package com.example.vforce.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for VForce360 Slack notifications.
 * Acts as the bridge between the workflow activities and the external Slack API/MQ.
 */
@Component
public class SlackNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    /**
     * Sends a text message to the configured Slack channel.
     * Implementation would use Slack Web API or IBM MQ link.
     *
     * @param messageBody The formatted message body to send.
     */
    public void sendMessage(String messageBody) {
        // Implementation for TDD Green Phase:
        // We verify the message contains the expected content.
        // The actual HTTP call is abstracted away behind this adapter.
        
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be blank");
        }

        // Simulate sending the message
        log.info("[SLACK ADAPTER] Sending message to #vforce360-issues: {}", messageBody);
        
        // In a real implementation, we would call:
        // slackWebhookClient.post(url, messageBody);
        // or jmsTemplate.send("slack.out", session -> session.createTextMessage(messageBody));
    }
}
