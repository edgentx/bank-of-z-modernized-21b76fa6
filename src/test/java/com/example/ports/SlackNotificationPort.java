package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body The formatted message body.
     * @return true if the API accepts the request, false otherwise.
     */
    boolean postMessage(String channel, String body);
}
