package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Allows verification of sent messages without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
