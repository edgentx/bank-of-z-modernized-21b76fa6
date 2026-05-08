package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for Slack Notification.
 * Implements the port to capture output for verification in tests.
 * Ensures no real HTTP calls are made during unit testing.
 */
public class SlackNotificationPortMock implements SlackNotificationPort {

    private boolean called = false;
    private String capturedBody;

    @Override
    public void send(String messageBody) {
        this.called = true;
        this.capturedBody = messageBody;
        // System.out.println("[Mock Slack] Sending: " + messageBody); // Debugging
    }

    public boolean wasCalled() {
        return called;
    }

    public String getCapturedBody() {
        return capturedBody;
    }

    public void reset() {
        this.called = false;
        this.capturedBody = null;
    }
}