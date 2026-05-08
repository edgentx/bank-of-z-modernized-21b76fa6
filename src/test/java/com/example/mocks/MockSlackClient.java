package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to allow assertion on content.
 */
public class MockSlackClient implements SlackPort {
    public static final class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        sentMessages.add(new Message(channel, messageBody));
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public Message getLastMessage() {
        if (sentMessages.isEmpty()) {
            throw new IllegalStateException("No messages sent");
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void reset() {
        sentMessages.clear();
    }
}