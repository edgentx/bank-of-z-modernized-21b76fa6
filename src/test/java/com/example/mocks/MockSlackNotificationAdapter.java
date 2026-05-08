package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to memory for verification.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    public final List<Message> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String message) {
        // Simulate latency or logic if needed
        postedMessages.add(new Message(channel, message));
    }

    public void clear() {
        postedMessages.clear();
    }

    public record Message(String channel, String content) {}
}
