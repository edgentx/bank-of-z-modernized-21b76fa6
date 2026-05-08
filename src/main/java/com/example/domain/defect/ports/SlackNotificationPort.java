package com.example.domain.defect.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstracts the Slack API client to allow for mocking during testing.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param body The text content to be sent in the message body.
     */
    void send(String body);
}
