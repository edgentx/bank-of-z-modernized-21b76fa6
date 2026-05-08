package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures payloads to verify contents without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> receivedPayloads = new ArrayList<>();

    @Override
    public void sendNotification(String payload) {
        // Simulate successful transmission by storing the payload
        this.receivedPayloads.add(payload);
    }

    /**
     * Retrieves the last payload received.
     */
    public String getLastPayload() {
        if (receivedPayloads.isEmpty()) {
            throw new IllegalStateException("No payloads received");
        }
        return receivedPayloads.get(receivedPayloads.size() - 1);
    }

    /**
     * Clears the mock history.
     */
    public void clear() {
        receivedPayloads.clear();
    }
}