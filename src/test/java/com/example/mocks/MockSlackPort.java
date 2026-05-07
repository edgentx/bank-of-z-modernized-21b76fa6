package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to allow verification in tests.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        sentMessages.add(messageBody);
    }

    public void verifyMessageContains(String substring) {
        boolean found = sentMessages.stream()
            .anyMatch(msg -> msg.contains(substring));
        if (!found) {
            throw new AssertionError("Expected message to contain: " + substring + ", but got: " + sentMessages);
        }
    }

    public void clear() {
        sentMessages.clear();
    }
}
