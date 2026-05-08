package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String channel, String body) {
        if (shouldFail) {
            return false;
        }
        messages.add(new Message(channel, body));
        return true;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) {
            throw new IllegalStateException("No messages sent");
        }
        return messages.get(messages.size() - 1);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}