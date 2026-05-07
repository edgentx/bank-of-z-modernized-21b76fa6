package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing.
 * Captures messages sent during test execution to verify contents.
 */
public class MockSlackNotifier implements SlackNotifier {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("messageBody cannot be null");
        }
        // Simulate basic validation
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to verify if a specific URL was present in any of the sent messages.
     */
    public boolean wasUrlSent(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
