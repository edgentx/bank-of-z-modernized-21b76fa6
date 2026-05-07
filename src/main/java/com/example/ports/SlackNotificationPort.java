package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This abstraction allows us to mock the Slack API in tests.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The formatted body of the message
     * @throws RuntimeException if the notification fails
     */
    void postMessage(String channel, String messageBody);
}
