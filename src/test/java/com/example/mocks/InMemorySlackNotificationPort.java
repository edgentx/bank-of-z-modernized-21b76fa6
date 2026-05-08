package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for assertion.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String body) {
        messages.add(body);
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
