package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotifier.
 * Captures messages in memory for testing without external I/O.
 */
public class MockSlackNotifier implements SlackNotifier {
    public static class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void send(String channel, String messageBody) {
        // Capture message for verification
        this.messages.add(new Message(channel, messageBody));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String substring) {
        return messages.stream()
            .anyMatch(m -> m.body.contains(substring));
    }
}
