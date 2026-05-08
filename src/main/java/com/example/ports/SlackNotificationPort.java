package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by temporal workflows to report defects.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body text.
     * @return true if the message was successfully sent.
     */
    boolean sendMessage(String channel, String body);
}