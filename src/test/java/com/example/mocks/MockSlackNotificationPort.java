package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent during workflow execution to verify content without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<SlackMessage> sentMessages = new ArrayList<>();

    public record SlackMessage(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Simulate network latency or processing if necessary
        this.sentMessages.add(new SlackMessage(channel, messageBody));
    }

    public void reset() {
        sentMessages.clear();
    }
}