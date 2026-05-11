package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the SlackNotificationPort.
 * This adapter handles the actual connection to Slack.
 * Currently logs to console for defect verification purposes.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void send(String channel, String body) {
        // In a production environment, this would use the Slack Java SDK or an HTTP client.
        // For the purpose of validating the defect report (VW-454),
        // we log the output to verify the URL construction logic.
        log.info("Sending to Slack [{}]: {}", channel, body);
    }
}
