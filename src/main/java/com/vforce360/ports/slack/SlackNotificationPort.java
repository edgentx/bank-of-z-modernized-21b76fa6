package com.vforce360.ports.slack;

/**
 * Port interface for sending notifications to Slack.
 * This isolates the core logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g., "C0123456789").
     * @param messageBody The content of the message (Markdown formatted).
     * @return true if the message was successfully sent, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}
