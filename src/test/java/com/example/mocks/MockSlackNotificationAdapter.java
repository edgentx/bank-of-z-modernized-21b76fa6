package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores the last sent payload in memory to allow assertions.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private String lastPayload;
    private boolean called = false;

    @Override
    public boolean send(String payload) {
        this.lastPayload = payload;
        this.called = true;
        // Always return true to simulate success in happy path tests
        return true;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastPayload() {
        return lastPayload;
    }

    public void reset() {
        this.lastPayload = null;
        this.called = false;
    }
}
