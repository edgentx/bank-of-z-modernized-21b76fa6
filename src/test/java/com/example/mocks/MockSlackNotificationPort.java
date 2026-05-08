package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content in assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String message) {
        messages.add(new Message(channel, message));
    }

    public record Message(String channel, String content) {}

    public boolean containsUrlInChannel(String channel, String urlFragment) {
        return messages.stream()
                .filter(m -> m.channel().equals(channel))
                .anyMatch(m -> m.content().contains(urlFragment));
    }
}
