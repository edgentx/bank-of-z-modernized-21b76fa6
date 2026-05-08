package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort.
 * Stores messages in memory for verification during tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channelId, String messageBody) {
        messages.put(channelId, messageBody);
    }

    @Override
    public String getLastMessageBody(String channelId) {
        return messages.get(channelId);
    }

    /**
     * Helper to clear the state between tests if necessary.
     */
    public void clear() {
        messages.clear();
    }
}