package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock Adapter for SlackNotificationPort.
 * Used in tests to verify that messages are generated correctly without external I/O.
 */
public class SpySlackNotificationAdapter implements SlackNotificationPort {

    private boolean called = false;
    private String capturedBody = "";

    @Override
    public void send(String messageBody) {
        this.called = true;
        this.capturedBody = messageBody;
        // No real network call is made.
    }

    public boolean wasCalled() {
        return called;
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}
