package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for unit testing.
 * Captures sent messages to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<Message> sentMessages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String messageBody) {
        this.sentMessages.add(new Message(channel, messageBody));
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper assertion for regression tests.
     * Verifies that the body contains a GitHub URL matching the expected format.
     */
    public boolean hasMessageWithGitHubLink(String channel) {
        return sentMessages.stream()
            .filter(m -> m.channel().equals(channel))
            .anyMatch(m -> m.body().contains("http") && m.body().contains("github.com"));
    }
}