package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to allow assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String body) {
        // In a real mock framework we might intercept, but here we just capture.
        // System.out.println("[MockSlack] Captured: " + body);
        this.messages.add(body);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String substring) {
        return messages.stream().anyMatch(msg -> msg.contains(substring));
    }
}