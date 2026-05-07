package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify expected content (e.g., GitHub URLs) without external calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> capturedMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean send(String messageBody) {
        capturedMessages.add(messageBody);
        return shouldSucceed;
    }

    public List<String> getCapturedMessages() {
        return new ArrayList<>(capturedMessages);
    }

    public void reset() {
        capturedMessages.clear();
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
}
