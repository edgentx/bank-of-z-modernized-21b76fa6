package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Implementations (Adapters) will handle the actual Webhook/API integration with Slack.
 */
public interface SlackNotifier {

    /**
     * Sends a notification message to a Slack channel.
     *
     * @param body The message content to send.
     */
    void sendNotification(String body);
}
