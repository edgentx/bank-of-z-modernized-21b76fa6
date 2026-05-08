package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier.
 * Captures messages sent to Slack to allow assertions in tests.
 */
public class MockSlackNotifier implements SlackNotifier {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String message) {
        messages.add(message);
    }

    public String getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public List<String> getAllMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
