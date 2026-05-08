package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages for verification instead of sending real HTTP requests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void sendMessage(String messageBody) {
        if (shouldFail) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}