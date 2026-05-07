package com.example.adapters;

import com.example.ports.SlackNotificationPort;

/**
 * Real adapter for Slack notifications.
 * Sends messages to Slack channels. In production, this would use the
 * Slack Web API client to post messages.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void postMessage(String channel, String body) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        // Real implementation would call slack API here.
        // System.out.println("[Slack] Posting to " + channel + ": " + body);
    }
}
