package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content and recipients.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String text;

        public Message(String channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }

    private final List<Message> messages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String channel, String text) {
        if (shouldFail) {
            return false;
        }
        messages.add(new Message(channel, text));
        return true;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}
