package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Defect Reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g. "#vforce360-issues").
     * @param body    The body of the message.
     * @return true if sending was successful, false otherwise.
     */
    boolean sendMessage(String channel, String body);

    /**
     * Gets the last message body sent to a specific channel.
     * Primarily used for testing/validation queries.
     *
     * @param channel The Slack channel ID or name.
     * @return The last message body string, or null if no messages were sent.
     */
    String getLastMessageBody(String channel);
}
