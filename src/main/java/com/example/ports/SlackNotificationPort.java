package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Concrete implementations will handle the actual HTTP webhook calls.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param messageBody The formatted content of the message.
     */
    void sendMessage(String channel, String messageBody);

    /**
     * Retrieves the last message body sent to the specific channel in the current context.
     * This is primarily used for testing/verification.
     *
     * @param channel The target channel.
     * @return The message body string, or null if no message was sent.
     */
    String getLastMessageBody(String channel);
}