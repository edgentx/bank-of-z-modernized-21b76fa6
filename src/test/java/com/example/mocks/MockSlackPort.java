package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to allow verification of content (e.g., presence of GitHub URL).
 */
public class MockSlackPort implements SlackPort {

    public static final class SlackMessage {
        public final String channel;
        public final String body;

        public SlackMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SlackMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        messages.add(new SlackMessage(channel, messageBody));
    }

    public List<SlackMessage> getMessages() {
        return messages;
    }

    public SlackMessage getLastMessage() {
        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages sent");
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}
