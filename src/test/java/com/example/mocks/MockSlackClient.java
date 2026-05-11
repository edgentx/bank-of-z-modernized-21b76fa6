package com.example.mocks;

import com.example.ports.SlackClientPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackClientPort.
 * Captures messages sent during tests to verify content without calling the real API.
 */
public class MockSlackClient implements SlackClientPort {

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
    public void postMessage(String channel, String body) {
        System.out.println("[MockSlackClient] Captured message for channel: " + channel);
        this.messages.add(new Message(channel, body));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public Message getLatestMessage() {
        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages captured");
        }
        return messages.get(messages.size() - 1);
    }
}
