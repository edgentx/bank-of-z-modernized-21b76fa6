package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores sent payloads to allow assertions on content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentPayloads = new ArrayList<>();

    @Override
    public void send(String payload) {
        // In a real mock we might verify formatting here, but storing is sufficient for verification in tests
        sentPayloads.add(payload);
    }

    public List<String> getSentPayloads() {
        return new ArrayList<>(sentPayloads);
    }

    public String getLatestPayload() {
        if (sentPayloads.isEmpty()) {
            throw new IllegalStateException("No payloads sent");
        }
        return sentPayloads.get(sentPayloads.size() - 1);
    }

    public void clear() {
        sentPayloads.clear();
    }
}
