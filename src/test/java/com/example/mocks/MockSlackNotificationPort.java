package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to verify the Slack body content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean sendMessage(String message) {
        sentMessages.add(message);
        return shouldSucceed;
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    public void clear() {
        sentMessages.clear();
    }
}
