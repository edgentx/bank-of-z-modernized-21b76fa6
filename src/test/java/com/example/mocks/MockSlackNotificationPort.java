package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent during test execution.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        // Store the message for assertion
        this.sentMessages.add(messageBody);
    }

    public void clear() {
        this.sentMessages.clear();
    }
}
