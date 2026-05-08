package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real HTTP Adapter for Slack.
 * In a production environment, this would use RestTemplate/WebClient to call the Slack API.
 * For defect VW-454, we assume this connects to #vforce360-issues.
 */
@Component
public class SlackHttpAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackHttpAdapter.class);

    @Override
    public boolean sendMessage(String channelId, String message) {
        // TODO: Implement actual Slack API call using RestTemplate or WebClient.
        // e.g., slackClient.postMessage(channelId, message);
        log.info("[MOCK] Sending message to Slack channel {}: {}", channelId, message);
        return true;
    }
}