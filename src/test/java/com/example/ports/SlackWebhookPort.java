package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackWebhookPort {
    /**
     * Sends a payload to the configured Slack webhook.
     * @param jsonPayload The formatted JSON string to send.
     */
    void send(String jsonPayload);
}
