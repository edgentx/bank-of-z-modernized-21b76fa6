package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal workflow to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param messageBody The formatted content of the message.
     */
    void sendMessage(String messageBody);

    /**
     * Retrieves the last message body sent to Slack.
     * This is primarily used for verification in testing environments.
     *
     * @return The last sent message body, or null if no message has been sent.
     */
    String getLastMessageBody();
}
