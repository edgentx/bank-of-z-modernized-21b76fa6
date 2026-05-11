package com.example.mocks;

/**
 * Port interface for Slack notifications.
 * Implemented by mocks in test scope, adapters in prod scope.
 */
public interface MockSlackNotificationPort {
    void sendMessage(String channel, String body);

    /**
     * Helper for tests to verify the last message body.
     */
    String getLastMessageBody();
}