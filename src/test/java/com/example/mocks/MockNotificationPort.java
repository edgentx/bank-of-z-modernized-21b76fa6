package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures messages sent to channels to verify content without real I/O.
 */
public class MockNotificationPort implements NotificationPort {

    public final List<Message> sentMessages = new ArrayList<>();

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        // Store the message for verification in tests
        sentMessages.add(new Message(channelId, messageBody));
        return true; // Simulate success
    }

    public record Message(String channelId, String body) {}

    /**
     * Helper to check if a specific URL was sent to a specific channel.
     */
    public boolean wasUrlSentToChannel(String url, String channelId) {
        return sentMessages.stream()
                .filter(m -> m.channelId().equals(channelId))
                .anyMatch(m -> m.body().contains(url));
    }

    public void clear() {
        sentMessages.clear();
    }
}
