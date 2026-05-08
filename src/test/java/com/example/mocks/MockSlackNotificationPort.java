package com.example.mocks;

import com.example.domain.validation.port.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last sent message body to allow assertions in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private boolean invoked = false;
    private String capturedBody;

    @Override
    public void send(String body) {
        this.invoked = true;
        this.capturedBody = body;
        // In a real mock, we might log this or do nothing.
        // System.out.println("[MockSlack] Captured: " + body);
    }

    public boolean wasInvoked() {
        return invoked;
    }

    public String getCapturedBody() {
        return capturedBody;
    }

    public void reset() {
        this.invoked = false;
        this.capturedBody = null;
    }
}
