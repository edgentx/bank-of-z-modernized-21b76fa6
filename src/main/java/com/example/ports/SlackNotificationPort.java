package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues").
     * @param messageBody The content of the message.
     * @return true if sending was considered successful, false otherwise.
     */
    boolean send(String channel, String messageBody);
}
