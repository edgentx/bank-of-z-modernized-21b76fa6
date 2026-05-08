package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages sent to Slack so we can assert on their content.
 */
public class InMemorySlackAdapter implements SlackNotificationPort {

    private final List<Message> messages = new ArrayList<>();

    public boolean wasCalled() {
        return !messages.isEmpty();
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).body();
    }

    public String getLastChannel() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).channel();
    }

    @Override
    public boolean sendMessage(String channel, String body) {
        messages.add(new Message(channel, body));
        return true;
    }

    private record Message(String channel, String body) {}
}