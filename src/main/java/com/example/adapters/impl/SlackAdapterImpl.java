package com.example.adapters.impl;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for Slack API.
 * Handles posting messages to channels.
 */
@Component
public class SlackAdapterImpl implements SlackPort {

    @Override
    public void postMessage(String channel, String body) {
        // In a real production environment, this would use the Slack Web API client
        // (e.g., using OkHttp or the official Slack Java SDK) to POST to
        // https://slack.com/api/chat.postMessage
        
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null or empty");
        }
        if (body == null) {
            // Slack allows empty messages, but usually we want content
            body = "";
        }

        // Simulate the side-effect of sending (logging could be added here)
        System.out.println(String.format("[SLACK OUT] %s: %s", channel, body));
    }
}
