package com.example.ports;

/**
 * Port for sending Slack notifications.
 * This interface must be implemented by the actual adapter and mocked in tests.
 */
public interface SlackPort {

    /**
     * Sends a notification to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The content of the message.
     */
    void sendMessage(String channel, String body);

    /**
     * Retrieves the last message body sent to the specified channel during the test context.
     * Used for assertions in tests.
     *
     * @param channel The target channel.
     * @return The last message body sent, or null if no message has been sent.
     */
    String getLastMessageBody(String channel);
}