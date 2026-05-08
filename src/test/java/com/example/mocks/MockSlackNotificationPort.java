package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the Slack port for testing.
 * Stores messages in memory to allow verification without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> sentMessages = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        if (shouldFail) {
            return false;
        }
        sentMessages.put(channel, messageBody);
        return true;
    }

    @Override
    public String getLastMessageBody(String channel) {
        return sentMessages.get(channel);
    }

    // Test utility methods
    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public void clear() {
        sentMessages.clear();
    }
}
