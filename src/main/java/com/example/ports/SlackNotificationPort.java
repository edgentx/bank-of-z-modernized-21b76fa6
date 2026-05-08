package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a configured Slack channel.
     *
     * @param channel The name of the channel (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @return true if sending was successful, false otherwise.
     */
    boolean sendMessage(String channel, String messageBody);

    /**
     * Retrieves the last message body sent to the specified channel in the current test context.
     * This is primarily used for verification in mock implementations during testing.
     *
     * @param channel The channel name.
     * @return The last message body string, or null if no message was sent.
     */
    String getLastMessageBody(String channel);
}