package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify message formatting in defect reports.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted text content of the message.
     * @throws IllegalArgumentException if the messageBody is null or blank.
     */
    void postMessage(String channel, String messageBody);
}
