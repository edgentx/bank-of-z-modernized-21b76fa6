package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 */
public class MockSlackPort implements SlackPort {

    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        sentMessages.add(message);
    }

    public boolean contains(String text) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(text));
    }

    public String getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }
}
