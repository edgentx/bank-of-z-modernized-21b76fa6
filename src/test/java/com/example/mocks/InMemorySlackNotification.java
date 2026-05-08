package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures messages sent to Slack for verification in tests.
 */
public class InMemorySlackNotification implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void postMessage(String text) {
        // Capture the message body for verification
        messages.add(text);
    }

    public String getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
