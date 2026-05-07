package com.example.domain.slack.ports;

/**
 * Port interface for Slack notifications.
 */
public interface SlackNotifierPort {
    /**
     * Sends a notification message to a Slack channel.
     *
     * @param message The message payload to send.
     */
    void sendNotification(String message);
}