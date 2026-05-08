package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory to allow verification of content and state.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();
    private boolean simulateSuccess = true;

    @Override
    public boolean sendMessage(String channel, String body) {
        messages.put(channel, body);
        return simulateSuccess;
    }

    @Override
    public String getLastMessageBody(String channel) {
        return messages.get(channel);
    }

    /**
     * Helper method for tests to inject failure scenarios if needed,
     * though the current story focuses on content validation.
     */
    public void setSimulateSuccess(boolean success) {
        this.simulateSuccess = success;
    }

    /**
     * Clears the message history. Useful for test isolation.
     */
    public void clear() {
        messages.clear();
    }
}
