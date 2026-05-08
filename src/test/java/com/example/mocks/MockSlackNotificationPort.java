package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        // In a real mock, we might just store this. Here we emulate no-op I/O.
        sentMessages.add(messageBody);
    }

    public boolean received(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
