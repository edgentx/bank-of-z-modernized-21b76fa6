package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * This allows us to mock the Slack Web API in tests.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification message to a channel.
     * @param messageBody The markdown/text body of the message
     */
    void notify(String messageBody);
}
