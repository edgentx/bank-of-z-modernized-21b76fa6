package com.example.ports;

import java.util.Map;

/**
 * Port for sending notifications (e.g., Slack, Email).
 * Used to decouple the domain logic from specific notification implementations.
 */
public interface NotificationPort {

    /**
     * Sends a notification to a specific channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body The message body/content.
     */
    void send(String channel, String body);
}
