package com.example.adapters;

import com.example.ports.SlackNotificationPort;

/**
 * Mock Adapter for Slack Notification.
 * Allows tests to verify if messages were sent and inspect their content.
 */
public class FakeSlackNotificationPort implements SlackNotificationPort {

    private String capturedBody;
    private boolean called = false;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        this.capturedBody = messageBody;
        this.called = true;
        // Simulate success
        return true;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getCapturedMessageBody() {
        return capturedBody;
    }

    public void reset() {
        this.capturedBody = null;
        this.called = false;
    }
}
