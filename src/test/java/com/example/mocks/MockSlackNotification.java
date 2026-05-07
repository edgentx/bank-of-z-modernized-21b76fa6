package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without real I/O.
 */
public class MockSlackNotification implements SlackNotificationPort {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // In a real test spy, we might throw exceptions if configured to do so.
        // For now, just capture the message.
        this.messages.add(messageBody);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
