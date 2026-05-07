package com.example.adapters;

import com.example.ports.SlackNotificationPort;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Real implementation of SlackNotificationPort.
 * Would perform an HTTP POST to the Slack Webhook URL.
 */
public class RealSlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = Logger.getLogger(RealSlackNotificationAdapter.class.getName());

    @Override
    public void sendDefectNotification(String defectId, String message, URI githubUrl) {
        String body = String.format(
            "Defect Report: %s\nMessage: %s\nGitHub Issue: %s",
            defectId,
            message,
            (githubUrl != null ? githubUrl.toString() : "PENDING")
        );
        
        // In a real implementation, we would use RestTemplate or WebClient to POST to Slack.
        logger.info("Sending Slack notification: " + body);
    }
}
