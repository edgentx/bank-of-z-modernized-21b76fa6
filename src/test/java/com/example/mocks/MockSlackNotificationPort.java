package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the Slack notification port.
 * Stores messages in memory for test verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Simulate basic latency or processing if needed, but keep simple for unit tests
        messages.put(channel, messageBody);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return messages.get(channel);
    }

    /**
     * Helper method for tests to clear state if necessary.
     */
    public void clear() {
        messages.clear();
    }
}
