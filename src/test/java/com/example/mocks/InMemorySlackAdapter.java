package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Stores messages in memory for assertion.
 */
public class InMemorySlackAdapter implements SlackPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // Simulate network latency or processing if needed
        this.messages.add(messageBody);
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) return "";
        return messages.get(messages.size() - 1);
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
