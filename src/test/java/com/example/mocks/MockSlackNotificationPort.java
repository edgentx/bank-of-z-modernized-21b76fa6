package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content (VW-454).
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String body) {
        sentMessages.add(body);
    }

    public String getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void clear() {
        sentMessages.clear();
    }
}
