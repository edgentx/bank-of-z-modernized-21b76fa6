package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstracts the specific Slack client implementation (e.g. OkHttp).
 */
public interface SlackPort {
    void sendNotification(String webhookUrl, String jsonPayload);
}
