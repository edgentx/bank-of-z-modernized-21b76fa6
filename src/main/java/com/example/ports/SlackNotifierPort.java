package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain logic to abstract the Slack API client.
 */
public interface SlackNotifierPort {
    /**
     * Sends a notification message to Slack.
     * @param body The formatted message body.
     */
    void notify(String body);
}
