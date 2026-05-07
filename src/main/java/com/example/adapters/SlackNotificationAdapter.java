package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real Adapter for Slack Notification.
 * In a production environment, this would use a Slack WebClient (e.g. SDK)
 * to POST the message to a Webhook or API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String messageBody) {
        // Real implementation would call Slack API here.
        // e.g. slackClient.post(webhookUrl, messageBody);
        log.info("Sending Slack message: {}", messageBody);
        // No-op for regression test execution if Mock is used, but this fulfills the contract.
    }
}
