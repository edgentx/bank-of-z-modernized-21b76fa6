package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the validation workflow to alert users of defects.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to the configured Slack channel.
     * @param messageBody The formatted body of the message.
     */
    void sendMessage(String messageBody);
}
