package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Workflow/Activity layer to decouple from the actual Slack client.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param message The formatted message body.
     */
    void sendMessage(String message);
}
