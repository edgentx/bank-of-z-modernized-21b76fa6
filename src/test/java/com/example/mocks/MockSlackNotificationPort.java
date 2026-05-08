package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures payloads sent to Slack for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentPayloads = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean send(String payload) {
        if (shouldFail) return false;
        sentPayloads.add(payload);
        return true;
    }

    public List<String> getSentPayloads() {
        return new ArrayList<>(sentPayloads);
    }

    public void reset() {
        sentPayloads.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}