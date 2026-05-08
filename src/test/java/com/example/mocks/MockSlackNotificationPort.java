package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of {@link com.example.ports.SlackNotificationPort} for testing.
 * Captures messages sent to Slack for assertion.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<SlackMessage> messages = new ArrayList<>();

    public record SlackMessage(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String messageBody) {
        this.messages.add(new SlackMessage(channel, messageBody));
    }

    public List<SlackMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public SlackMessage getLastMessage() {
        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages captured");
        }
        return messages.get(messages.size() - 1);
    }
}
