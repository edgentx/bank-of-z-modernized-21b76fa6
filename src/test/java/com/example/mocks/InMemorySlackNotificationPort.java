package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without network calls.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channelId;
        public final String message;

        public PostedMessage(String channelId, String message) {
            this.channelId = channelId;
            this.message = message;
        }
    }

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channelId, String message) {
        // Simulate basic validation logic found in real adapters
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be blank");
        }
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        this.messages.add(new PostedMessage(channelId, message));
    }

    public List<PostedMessage> getMessages() {
        return List.copyOf(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean containsUrl(String url) {
        return messages.stream()
                .anyMatch(pm -> pm.message.contains(url));
    }
}
