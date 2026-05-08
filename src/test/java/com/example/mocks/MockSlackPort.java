package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to verify content in assertions.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // In a real mock, we might just store this. 
        // This simulates the external API call without network I/O.
        this.messages.add(messageBody);
    }

    public String getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}