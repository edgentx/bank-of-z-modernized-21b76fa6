package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotifier {
    /**
     * Sends a notification payload to the configured Slack channel.
     * @param payload the message structure to send
     */
    void notify(SlackMessagePayload payload);

    /**
     * DTO for the Slack message.
     */
    record SlackMessagePayload(String channel, String body) {}
}
