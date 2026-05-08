package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures payloads sent to Slack to allow validation.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> payloads = new ArrayList<>();
    private boolean shouldThrowException = false;

    @Override
    public void send(String payload) {
        if (shouldThrowException) {
            throw new IllegalArgumentException("Slack service unavailable");
        }
        payloads.add(payload);
    }

    public List<String> getPayloads() {
        return new ArrayList<>(payloads);
    }

    public void clear() {
        payloads.clear();
    }

    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }
}
