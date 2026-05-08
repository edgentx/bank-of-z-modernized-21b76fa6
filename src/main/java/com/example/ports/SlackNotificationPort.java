package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to abstract the external Slack API interaction.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param message The formatted message body.
     */
    void send(String message);

    /**
     * Retrieves the last message sent to the mock channel.
     * (Primarily used for verification in testing/verification scenarios)
     *
     * @return The last message string.
     */
    String getLastMessage();
}
