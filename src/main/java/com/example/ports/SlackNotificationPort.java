package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Defect Workflow to report status.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param body The message body content
     */
    void sendMessage(String channel, String body);
}
