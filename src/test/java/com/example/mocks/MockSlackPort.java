package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to memory for assertion.
 */
public class MockSlackPort implements SlackPort {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        // Prevent nulls in tests
        if (message == null) {
            messages.add("");
        } else {
            messages.add(message);
        }
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}