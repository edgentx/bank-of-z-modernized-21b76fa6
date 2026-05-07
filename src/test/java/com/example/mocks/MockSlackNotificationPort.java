package com.example.mocks;

/**
 * Mock interface for Slack notifications.
 * Used to verify that the system attempts to send the correct data
 * without actually calling the Slack API.
 */
public interface MockSlackNotificationPort {
    void sendNotification(String channel, String body);

    /**
     * Helper for tests to check if the URL was in the body.
     */
    boolean lastBodyContains(String text);
}