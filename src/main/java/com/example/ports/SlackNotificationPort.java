package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implementations will handle the actual HTTP call to Slack Web API.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param message The formatted message body to send.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean sendMessage(String message);
}
