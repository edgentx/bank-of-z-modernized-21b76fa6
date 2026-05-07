package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent payloads to verify behavior in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    private final List<Map<String, String>> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(Map<String, String> messagePayload) {
        this.sentMessages.add(messagePayload);
    }

    public List<Map<String, String>> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void reset() {
        sentMessages.clear();
    }

    public boolean wasCalledWithGitHubUrl(String url) {
        return sentMessages.stream()
                .anyMatch(msg -> url.equals(msg.get("githubUrl")));
    }
}