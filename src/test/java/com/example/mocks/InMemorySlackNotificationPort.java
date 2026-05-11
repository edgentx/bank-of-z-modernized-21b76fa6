package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last sent body to verify expectations.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private String capturedBody;
    private boolean called = false;

    @Override
    public boolean sendMessage(String channel, String body) {
        this.called = true;
        this.capturedBody = body;
        // Simulate success
        return true;
    }

    public String getCapturedBody() {
        return capturedBody;
    }

    public boolean wasCalled() {
        return called;
    }
}
