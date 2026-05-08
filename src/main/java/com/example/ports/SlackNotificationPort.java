package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification with the given body text.
     * @param body The message body content.
     */
    void send(String body);
}
