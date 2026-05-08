package com.example.mocks;

import com.example.ports.SlackNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock Adapter Pattern Implementation.
 * Simulates the Slack API without performing real network I/O.
 * Stores payloads in memory for test assertion.
 */
public class FakeSlackNotifier implements SlackNotifier {

    private final List<String> capturedPayloads = new ArrayList<>();
    private boolean shouldThrowException = false;

    @Override
    public void sendNotification(String payload) {
        if (shouldThrowException) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        // In a real scenario, this would do an HTTP POST.
        // Here we just store it to verify the output.
        this.capturedPayloads.add(payload);
    }

    public List<String> getCapturedPayloads() {
        return new ArrayList<>(capturedPayloads);
    }

    public void reset() {
        capturedPayloads.clear();
    }

    public void setShouldThrowException(boolean shouldThrow) {
        this.shouldThrowException = shouldThrow;
    }
}
