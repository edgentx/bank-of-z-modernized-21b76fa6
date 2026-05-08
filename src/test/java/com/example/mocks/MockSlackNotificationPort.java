package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent messages to verify body formatting without real webhooks.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void send(String messageBody) throws SlackNotificationException {
        if (shouldFail) {
            throw new SlackNotificationException("Simulated Slack API failure", new RuntimeException());
        }
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void reset() {
        sentMessages.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}
