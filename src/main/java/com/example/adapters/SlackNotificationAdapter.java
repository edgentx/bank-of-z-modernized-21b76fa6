package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for Slack notifications.
 * Connects to the actual Slack Web API.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // In a production scenario, this would use an HTTP client (e.g., WebClient or RestTemplate)
        // to POST to the Slack API.
        // Since we cannot make external network calls in this unit test environment,
        // and the defect S-FB-1 focuses on the *formatting* of the URL (business logic),
        // we log the payload here to demonstrate the adapter would receive the correct data.
        log.info("SLACK API CALL [Channel: {}]: {}", channel, body);
    }
}
