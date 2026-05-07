package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory to allow verification of content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> channelMessages = new HashMap<>();
    private boolean simulateFailure = false;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        if (simulateFailure) {
            return false;
        }
        channelMessages.put(channelId, messageBody);
        return true;
    }

    @Override
    public String getLastMessageBody(String channelId) {
        return channelMessages.get(channelId);
    }

    /**
     * Helper method for tests to verify the message contains the GitHub URL.
     */
    public boolean lastMessageContainsUrl(String channelId, String url) {
        String body = getLastMessageBody(channelId);
        return body != null && body.contains(url);
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    public void clear() {
        channelMessages.clear();
    }
}
