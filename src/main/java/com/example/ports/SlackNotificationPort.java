package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues").
     * @param body    The message body content.
     * @throws SlackNotificationException if the notification fails.
     */
    void sendNotification(String channel, String body);

    class SlackNotificationException extends RuntimeException {
        public SlackNotificationException(String message) {
            super(message);
        }
    }
}
