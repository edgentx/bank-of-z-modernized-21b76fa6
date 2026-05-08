package com.example.ports;

/**
 * Port for sending Slack notifications.
 * This abstraction allows us to mock Slack in tests without external network calls.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message (formatted text)
     */
    void sendNotification(String channel, String messageBody);
}
