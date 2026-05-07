package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock Adapter for Slack Notification.
 * Used in Testing to capture output without calling real APIs.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String capturedBody;
    private boolean notifyCalled = false;

    @Override
    public void notify(String messageBody) {
        this.notifyCalled = true;
        this.capturedBody = messageBody;
    }

    // Test Inspection Methods
    public boolean wasNotifyCalled() {
        return notifyCalled;
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}