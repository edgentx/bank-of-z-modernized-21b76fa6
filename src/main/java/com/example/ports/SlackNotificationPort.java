package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Defect Aggregate workflow to notify the engineering team.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param body    The formatted message body.
     */
    void sendMessage(String channel, String body);
}