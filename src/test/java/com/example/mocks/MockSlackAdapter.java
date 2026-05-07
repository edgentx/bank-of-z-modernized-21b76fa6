package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackPort for testing.
 * Stores messages in memory to allow verification without real I/O.
 */
public class MockSlackAdapter implements SlackPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String body) {
        // Store the message so tests can verify the content
        messages.put(channel, body);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return messages.get(channel);
    }

    /**
     * Clears the message history. Useful for setup/teardown.
     */
    public void clear() {
        messages.clear();
    }
}