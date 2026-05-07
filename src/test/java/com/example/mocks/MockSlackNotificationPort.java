package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent during the test for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // In a real mock, we might validate input types here, but we mostly just capture it.
        this.sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean hasReceivedMessageContaining(String substring) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(substring));
    }

    public void clear() {
        sentMessages.clear();
    }
}
