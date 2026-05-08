package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages to verify content during tests (TDD Red/Green phase).
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String body) {
        messages.add(new Message(channel, body));
    }

    public boolean receivedMessageContaining(String text) {
        return messages.stream()
                .anyMatch(msg -> msg.body().contains(text));
    }
}