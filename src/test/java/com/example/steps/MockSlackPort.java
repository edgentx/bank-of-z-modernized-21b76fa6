package com.example.steps;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock Adapter for Slack.
 * Stores messages in memory for verification during testing.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String body) {
        // Simulate success or capture output
        this.messages.add(body);
    }

    public boolean wasCalled() {
        return !messages.isEmpty();
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
