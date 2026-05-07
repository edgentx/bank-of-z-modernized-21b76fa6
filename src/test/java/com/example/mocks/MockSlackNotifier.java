package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing.
 * Stores messages in memory to verify content.
 */
public class MockSlackNotifier implements SlackNotifier {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String message) {
        messages.add(message);
    }

    public String getLastPostedMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
