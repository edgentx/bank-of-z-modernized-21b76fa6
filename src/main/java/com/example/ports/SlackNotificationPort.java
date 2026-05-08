package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstraction used to allow mocking in tests and switching implementations.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted message body
     * @throws IllegalArgumentException if channel or body is invalid
     * @throws RuntimeException if the external API call fails
     */
    void postMessage(String channel, String messageBody);
}
