package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock for Slack notifications.
 * Used in testing to capture messages without external I/O.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String messageBody) {
        // Simulate basic validation
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be blank");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        this.messages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public PostedMessage findFirstByChannel(String channel) {
        return messages.stream()
                .filter(m -> m.channel.equals(channel))
                .findFirst()
                .orElse(null);
    }
}
