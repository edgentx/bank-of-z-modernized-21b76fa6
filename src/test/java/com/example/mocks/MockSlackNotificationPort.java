package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures published messages for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<PublishedMessage> messages = new ArrayList<>();

    @Override
    public void publish(String channel, Map<String, Object> payload) {
        // Capture the call data for assertions
        messages.add(new PublishedMessage(channel, payload));
    }

    public void reset() {
        messages.clear();
    }

    public record PublishedMessage(String channel, Map<String, Object> payload) {}
}
