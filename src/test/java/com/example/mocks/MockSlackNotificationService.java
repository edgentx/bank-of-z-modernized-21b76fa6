package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock Adapter for SlackNotificationPort.
 * Used in tests to capture output without making real HTTP calls.
 */
public class MockSlackNotificationService implements SlackNotificationPort {

    private boolean called = false;
    private String lastChannel;
    private String lastBody;

    @Override
    public void sendMessage(String channel, String messageBody) {
        this.called = true;
        this.lastChannel = channel;
        this.lastBody = messageBody;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastMessageBody() {
        return lastBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public void reset() {
        this.called = false;
        this.lastBody = null;
        this.lastChannel = null;
    }
}
