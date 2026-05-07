package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures payloads sent to Slack without making external HTTP calls.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> payloads = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean send(String payload) {
        this.payloads.add(payload);
        return shouldSucceed;
    }

    public List<String> getPayloads() {
        return new ArrayList<>(payloads);
    }

    public void clear() {
        payloads.clear();
    }

    public void setShouldSucceed(boolean flag) {
        this.shouldSucceed = flag;
    }
}