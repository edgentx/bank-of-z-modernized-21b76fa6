package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for Slack notifications.
 * Stores messages in memory for verification during testing.
 * Implements the required Port interface.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final Map<String, String> sentMessages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Simulate network latency or processing if necessary
        // For TDD, we store it simply.
        sentMessages.put(channel, messageBody);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return sentMessages.get(channel);
    }

    /**
     * Helper method for tests to verify if a message was sent.
     */
    public boolean hasReceivedMessage(String channel) {
        return sentMessages.containsKey(channel);
    }

    public void clear() {
        sentMessages.clear();
    }
}