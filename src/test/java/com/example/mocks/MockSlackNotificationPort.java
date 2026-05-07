package com.example.mocks;

import com.example.domain.vforce360.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Stores payloads in memory for test verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> payloads = new ArrayList<>();

    @Override
    public boolean send(String payload) {
        if (payload == null) return false;
        payloads.add(payload);
        return true;
    }

    @Override
    public String getLastPayload() {
        if (payloads.isEmpty()) return null;
        return payloads.get(payloads.size() - 1);
    }

    public void clear() {
        payloads.clear();
    }

    public int getCallCount() {
        return payloads.size();
    }
}
