package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures sent messages for verification.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        System.out.println("[MockSlack] Sending: " + message);
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean containsMessage(String fragment) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(fragment));
    }

    public void reset() {
        sentMessages.clear();
    }
}