package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This decouples the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message, expected to contain the GitHub URL.
     * @return true if the API call indicates success, false otherwise.
     */
    boolean postMessage(String channel, String messageBody);
}
