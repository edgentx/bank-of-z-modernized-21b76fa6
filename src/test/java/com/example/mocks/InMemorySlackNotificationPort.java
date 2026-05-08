package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotificationPort.
 * Stores messages in memory for test assertions.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String body) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null or empty");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack body cannot be null or empty");
        }
        this.messages.add(new Message(channel, body));
    }

    public String getLastChannel() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).channel();
    }

    public String getLastBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).body();
    }

    public void clear() {
        messages.clear();
    }
}