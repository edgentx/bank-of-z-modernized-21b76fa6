package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The Slack channel ID or name.
     * @param body    The message body content.
     * @throws NotificationException if the send operation fails.
     */
    void sendMessage(String channel, String body);

    class NotificationException extends RuntimeException {
        public NotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
