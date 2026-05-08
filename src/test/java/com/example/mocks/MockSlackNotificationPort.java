package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to memory instead of calling the real Slack API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean containsUrl(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void clear() {
        sentMessages.clear();
    }
}
