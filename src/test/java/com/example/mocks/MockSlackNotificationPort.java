package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to memory instead of calling external APIs.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String message) {
        // In a real mock, we might record system state too, but capturing the message is key here.
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to check if the last message contained a specific substring.
     */
    public boolean lastMessageContains(String substring) {
        if (sentMessages.isEmpty()) return false;
        return sentMessages.get(sentMessages.size() - 1).contains(substring);
    }
}