package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for Slack notifications.
 * Stores messages in memory to verify behavior without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null or empty");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        // In a real mock, we might want to simulate errors, but for default behavior we just store.
        this.messages.put(channel, messageBody);
    }

    @Override
    public String getLastMessage(String channel) {
        return this.messages.get(channel);
    }

    /**
     * Helper method for tests to check if a specific channel was ever called.
     */
    public boolean hasReceivedMessageForChannel(String channel) {
        return this.messages.containsKey(channel);
    }

    public void clear() {
        this.messages.clear();
    }
}
