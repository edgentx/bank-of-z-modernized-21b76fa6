package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a specific channel.
     *
     * @param channel The name of the channel (e.g., #vforce360-issues)
     * @param body    The message body content
     * @throws IllegalArgumentException if the channel is invalid
     * @throws NotificationFailedException if the API call fails
     */
    void sendNotification(String channel, String body);

    class NotificationFailedException extends RuntimeException {
        public NotificationFailedException(String message) {
            super(message);
        }
    }
}
