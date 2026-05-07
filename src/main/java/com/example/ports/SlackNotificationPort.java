package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to verify if a GitHub URL is included in the message body.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to Slack.
     * @param channel The target channel.
     * @param messageBody The body of the message.
     */
    void sendNotification(String channel, String messageBody);
}
