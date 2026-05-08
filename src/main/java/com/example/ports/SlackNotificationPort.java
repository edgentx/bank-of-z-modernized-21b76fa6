package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal worker logic.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body content.
     * @return true if the API accepted the request, false otherwise.
     */
    boolean postMessage(String channel, String body);
}
