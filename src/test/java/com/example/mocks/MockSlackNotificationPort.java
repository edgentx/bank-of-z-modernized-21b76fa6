package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages instead of sending them to Slack.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static final class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void send(String channel, String body) {
        // In a real test, we might verify the inputs immediately.
        // Here we store them for assertions later.
        this.messages.add(new Message(channel, body));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String channel, String substring) {
        return messages.stream()
                .anyMatch(m -> m.channel.equals(channel) && m.body.contains(substring));
    }
}