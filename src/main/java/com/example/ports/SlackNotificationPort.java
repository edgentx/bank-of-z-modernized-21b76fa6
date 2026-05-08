package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to validate the content of defect reports.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body The message body text.
     */
    void postMessage(String channel, String body);
}
