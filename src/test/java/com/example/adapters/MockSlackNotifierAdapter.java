package com.example.adapters;

import com.example.ports.SlackNotifierPort;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Captures the body passed to the notify method for assertions.
 */
public class MockSlackNotifierAdapter implements SlackNotifierPort {

    private String capturedBody;
    private boolean called = false;

    @Override
    public void notify(String body) {
        this.capturedBody = body;
        this.called = true;
        System.out.println("[MockSlack] Notified with: " + body);
    }

    public boolean wasCalled() {
        return called;
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}
