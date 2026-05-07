package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for the Slack Notification Port.
 * Records payloads sent during the test execution without performing I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentPayloads = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean sendNotification(String payload) {
        if (shouldFail) return false;
        sentPayloads.add(payload);
        return true;
    }

    /**
     * Returns the list of payloads captured during the test.
     */
    public List<String> getSentPayloads() {
        return sentPayloads;
    }

    /**
     * Utility to simulate API failures.
     */
    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    /**
     * Clears the captured history.
     */
    public void clear() {
        sentPayloads.clear();
    }
}
