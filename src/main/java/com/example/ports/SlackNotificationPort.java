package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by Temporal workflows to alert users.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body content
     */
    void sendNotification(String channel, String body);
}
