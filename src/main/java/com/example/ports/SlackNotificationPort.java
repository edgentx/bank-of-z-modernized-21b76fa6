package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port interface for sending Slack notifications.
 * This decouples the domain logic from the concrete Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to the configured Slack channel.
     *
     * @param payload The formatted message payload intended for Slack.
     */
    void send(String payload);
}
