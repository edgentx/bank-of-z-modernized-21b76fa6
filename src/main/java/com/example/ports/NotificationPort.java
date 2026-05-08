package com.example.ports;

import com.example.application.SlackMessage;

/**
 * Port interface for sending notifications (e.g., to Slack).
 * Used to decouple the workflow implementation from the specific transport mechanism.
 */
public interface NotificationPort {

    /**
     * Sends a message to the configured Slack channel.
     *
     * @param message The message payload to send.
     */
    void send(SlackMessage message);
}