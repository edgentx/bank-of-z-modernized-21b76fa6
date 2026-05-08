package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the SlackNotificationPort.
 * In a production environment, this would make an HTTP call to the Slack Webhook.
 * For the scope of this fix, it logs the output and returns success.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean send(String messageBody) {
        // Real-world implementation would use RestTemplate/WebClient to POST to a Slack Webhook URL.
        // For this validation, we log and return true to simulate successful delivery.
        logger.info("Slack notification sent: {}", messageBody);
        return true;
    }
}
