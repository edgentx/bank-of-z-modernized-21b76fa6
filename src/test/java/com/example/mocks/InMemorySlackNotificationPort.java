package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages posted to Slack for assertion.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final List<PostedMessage> messages = new ArrayList<>();

    public static record PostedMessage(String channel, String body) {}

    @Override
    public void postMessage(String channel, String messageBody) {
        // Basic validation mirroring a real implementation
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel cannot be null or empty");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("messageBody cannot be null or empty");
        }

        messages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return List.copyOf(messages);
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Helper method to verify if a specific URL was posted in any message to a specific channel.
     */
    public boolean wasUrlPostedToChannel(String channel, String url) {
        return messages.stream()
            .filter(m -> m.channel().equals(channel))
            .anyMatch(m -> m.body().contains(url));
    }
}
