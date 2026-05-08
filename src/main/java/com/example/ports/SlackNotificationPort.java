package com.example.ports;

/**
 * Port interface for sending Slack Notifications.
 * Abstracts the Slack Web API.
 */
public interface SlackNotificationPort {

    /**
     * Sends a formatted message to a channel.
     * @param message The message payload.
     */
    void sendNotification(SlackMessage message);

    /**
     * Simple DTO for the message payload.
     */
    class SlackMessage {
        private final String channel;
        private final String body;

        public SlackMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }

        public String getChannel() { return channel; }
        public String getBody() { return body; }
    }
}
