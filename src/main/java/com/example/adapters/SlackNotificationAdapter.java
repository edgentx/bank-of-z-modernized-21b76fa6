package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Real implementation of the Slack Notification Port.
 * Connects to the external Slack API.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void send(String message) {
        // Implementation for the actual Slack API call would go here.
        // For VW-454, we ensure the message passed in contains the GitHub URL.
        log.info("Sending Slack notification: {}", message);
        
        // Pseudo-code for actual implementation:
        // WebTestClient webClient = WebClient.create("https://slack.com/api");
        // webClient.post()...
    }
}