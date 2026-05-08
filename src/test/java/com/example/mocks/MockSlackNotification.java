package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory instead of calling the real Slack API.
 */
public class MockSlackNotification implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String message) {
        // Simulate successful send by storing the message
        this.messages.add(message);
    }

    @Override
    public String getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
