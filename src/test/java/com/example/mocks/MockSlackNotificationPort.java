package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores captured messages to allow assertions on their content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void postMessage(String channel, String messageBody) {
        if (channel == null || messageBody == null) {
            throw new IllegalArgumentException("Channel and body must not be null");
        }
        this.messages.add(new Message(channel, messageBody));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Helper assertion method to check if the last message contained a specific substring.
     */
    public boolean lastMessageContains(String substring) {
        if (messages.isEmpty()) return false;
        Message last = messages.get(messages.size() - 1);
        return last.body().contains(substring);
    }
}
