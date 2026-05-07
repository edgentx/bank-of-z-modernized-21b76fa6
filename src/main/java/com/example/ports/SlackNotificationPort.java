package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by Temporal workflows to alert users.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param message The formatted message body to send.
     * @return true if the message was accepted by the Slack client, false otherwise.
     */
    boolean send(String message);
}
