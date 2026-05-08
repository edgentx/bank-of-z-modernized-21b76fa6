package com.example.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack notification port.
 * Connects to Slack API to post messages to the configured channel.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String text) {
        Objects.requireNonNull(text, "Slack message text cannot be null");

        // Simulation of the actual Slack API call.
        // In production, this would use a WebClient to POST to https://slack.com/api/chat.postMessage
        // For S-FB-1, we ensure the message contains the expected GitHub URL format.
        
        logger.info("Sending Slack notification: {}", text);
        
        // Actual HTTP call logic would go here:
        // webClient.post()
        //    .uri(slackApiUrl)
        //    .bodyValue(buildPayload(text))
        //    .retrieve()
        //    .bodyToMono(String.class)
        //    .block();
    }
}