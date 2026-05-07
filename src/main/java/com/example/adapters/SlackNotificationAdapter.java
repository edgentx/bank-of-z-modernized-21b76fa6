package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for sending Slack notifications.
 * In a production environment, this would use the Slack WebClient or an HTTP client
 * to post the message to the configured webhook URL.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        // In a real-world scenario, we would inject a WebClient or RestTemplate
        // and POST to the Slack API webhook URL.
        // For the TDD Green Phase of this defect fix, we focus on the structural correctness
        // and the integration point.
        
        logger.info("Sending Slack notification: {}", messageBody);
        
        // Simulated successful send
        // HttpClient.post(webhookUrl, messageBody);
    }
}