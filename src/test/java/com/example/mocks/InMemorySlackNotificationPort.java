package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages instead of sending real HTTP requests.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public boolean sendMessage(String messageBody) {
        if (messageBody == null) {
            return false;
        }
        messages.add(messageBody);
        return true;
    }

    @Override
    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
