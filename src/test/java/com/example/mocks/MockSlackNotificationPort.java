package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures message bodies to verify content without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // Simulate recording the message instead of sending it
        this.sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public String getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void reset() {
        sentMessages.clear();
    }
}
