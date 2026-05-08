package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Implementations will handle the actual HTTP webhook calls.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to send.
     * @throws SlackNotificationException if the notification fails.
     */
    void send(String messageBody) throws SlackNotificationException;

    class SlackNotificationException extends RuntimeException {
        public SlackNotificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
