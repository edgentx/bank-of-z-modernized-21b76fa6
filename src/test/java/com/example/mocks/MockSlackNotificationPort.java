package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without network calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String body;

        public SentMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String body) {
        // Record the interaction for assertion in tests
        this.messages.add(new SentMessage(channel, body));
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String channel, String substring) {
        return messages.stream()
                .filter(m -> m.channel.equals(channel))
                .anyMatch(m -> m.body.contains(substring));
    }
}
