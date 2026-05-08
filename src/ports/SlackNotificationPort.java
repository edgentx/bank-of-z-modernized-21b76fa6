package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows/activities to report defects.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a configured Slack channel.
     *
     * @param body The formatted message body to send.
     */
    void sendMessage(String body);
}
