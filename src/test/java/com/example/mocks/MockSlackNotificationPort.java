package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotificationPort.
 * Captures messages sent during tests to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String text;

        public Message(String channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String text) {
        this.messages.add(new Message(channel, text));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasMessageContaining(String substring) {
        return messages.stream().anyMatch(m -> m.text.contains(substring));
    }
}
