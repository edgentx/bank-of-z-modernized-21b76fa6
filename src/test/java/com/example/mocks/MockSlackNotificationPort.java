package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory so tests can verify the content without calling the real Slack API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        // Simulate successful send
        messages.put(channelId, messageBody);
        return true;
    }

    @Override
    public String getLastMessageBody(String channelId) {
        return messages.get(channelId);
    }

    /**
     * Helper method for tests to clear state if needed.
     */
    public void clear() {
        messages.clear();
    }
}