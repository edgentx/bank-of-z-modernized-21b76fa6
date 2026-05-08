package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack in memory.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public record PostedMessage(String channel, String body) {}

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String messageBody) {
        this.messages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public PostedMessage getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }
}
