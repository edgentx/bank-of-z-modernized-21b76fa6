package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Allows assertions on what was sent.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private boolean sent = false;
    private String lastBody;
    private String lastChannel;

    @Override
    public void postMessage(String channelId, String text) {
        this.sent = true;
        this.lastChannel = channelId;
        this.lastBody = text;
    }

    public boolean wasNotificationSent() {
        return sent;
    }

    public String getLastSentBody() {
        return lastBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }
}