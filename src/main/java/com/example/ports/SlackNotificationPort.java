package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This decouples the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body content
     */
    void sendNotification(String channel, String body);
}
