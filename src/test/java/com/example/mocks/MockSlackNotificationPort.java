package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures payloads to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> capturedPayloads = new ArrayList<>();
    private boolean shouldThrowException = false;

    @Override
    public void send(String payload) throws Exception {
        if (shouldThrowException) {
            throw new Exception("Simulated Slack API failure");
        }
        capturedPayloads.add(payload);
    }

    public List<String> getCapturedPayloads() {
        return new ArrayList<>(capturedPayloads);
    }

    public void reset() {
        capturedPayloads.clear();
        shouldThrowException = false;
    }

    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }

    /**
     * Helper to check if any captured payload contains the specific text.
     */
    public boolean wasUrlSent(String url) {
        return capturedPayloads.stream()
                .anyMatch(payload -> payload.contains(url));
    }
}
