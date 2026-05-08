package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages in memory to verify content without network calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public boolean postMessage(String channel, String body) {
        messages.add(new Message(channel, body));
        return true;
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String channel, String substring) {
        return messages.stream()
            .filter(m -> m.channel().equals(channel))
            .anyMatch(m -> m.body().contains(substring));
    }

    public String getLastBodyForChannel(String channel) {
        return messages.stream()
            .filter(m -> m.channel().equals(channel))
            .reduce((a, b) -> b)
            .orElseThrow(() -> new AssertionError("No messages found for channel: " + channel))
            .body();
    }
}
