package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain layer to avoid direct dependency on the Slack API.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g., "C12345")
     * @param messageBody The content of the message (formatted text)
     * @return true if the API accepted the request, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);

    /**
     * Retrieves the last message body sent to a specific channel.
     * Used primarily for testing/verification in this context.
     *
     * @param channelId The ID of the channel.
     * @return The last message body string, or null if none exists.
     */
    String getLastMessageBody(String channelId);
}
