package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores the last payload locally for assertions without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastPayload;

    @Override
    public void send(String payload) {
        this.lastPayload = payload;
    }

    @Override
    public String getLastPayload() {
        return this.lastPayload;
    }

    /**
     * Utility method for tests to verify if the payload contains a specific substring.
     */
    public boolean lastPayloadContains(String substring) {
        return lastPayload != null && lastPayload.contains(substring);
    }

    public void reset() {
        this.lastPayload = null;
    }
}
