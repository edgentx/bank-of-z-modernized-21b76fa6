package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for SlackNotificationPort.
 * Captures message payloads for assertion in tests.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private boolean notifyCalled = false;
    private String capturedChannel;
    private String capturedBody;

    @Override
    public void postMessage(String channel, String body) {
        this.notifyCalled = true;
        this.capturedChannel = channel;
        this.capturedBody = body;
    }

    // Test inspection methods
    public boolean wasNotifyCalled() {
        return notifyCalled;
    }

    public String getCapturedBody() {
        return capturedBody;
    }

    public String getCapturedChannel() {
        return capturedChannel;
    }
}
