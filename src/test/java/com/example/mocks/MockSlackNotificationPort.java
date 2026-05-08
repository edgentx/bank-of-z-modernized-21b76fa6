package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to memory for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendNotification(String channel, String body) {
        this.messages.add(new Message(channel, body));
    }

    public String getLastBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).body();
    }
}
