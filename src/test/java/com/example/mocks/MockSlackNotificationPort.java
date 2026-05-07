package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages sent during the test lifecycle for assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channelId;
        public final String body;

        public SentMessage(String channelId, String body) {
            this.channelId = channelId;
            this.body = body;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SentMessage that = (SentMessage) o;
            return Objects.equals(channelId, that.channelId) && Objects.equals(body, that.body);
        }

        @Override
        public int hashCode() {
            return Objects.hash(channelId, body);
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channelId, String messageBody) {
        // Simulate real behavior: validate inputs
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be blank");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("messageBody cannot be null");
        }
        this.messages.add(new SentMessage(channelId, messageBody));
    }

    public List<SentMessage> getMessages() {
        return List.copyOf(messages);
    }

    public void clear() {
        messages.clear();
    }
}
