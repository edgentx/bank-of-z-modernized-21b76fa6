package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g. "C12345678").
     * @param messageBody The content of the message.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);

    /**
     * Retrieves the last message body sent to the channel.
     * This is primarily used for testing assertions to verify the content.
     *
     * @param channelId The ID of the channel.
     * @return The last message body string, or null if no message exists.
     */
    String getLastMessageBody(String channelId);
}