package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory so they can be verified during tests.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> channelMessages = new HashMap<>();

    @Override
    public void postMessage(String channel, String body) {
        // Simulate network latency or processing if needed
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Store the message for verification
        channelMessages.put(channel, body);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return channelMessages.get(channel);
    }

    /**
     * Helper method to clear the state between tests if needed.
     */
    public void clear() {
        channelMessages.clear();
    }
}
