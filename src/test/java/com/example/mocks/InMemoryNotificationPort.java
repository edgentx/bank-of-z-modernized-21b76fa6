package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures arguments to verify behavior without calling external APIs.
 */
public class InMemoryNotificationPort implements SlackNotificationPort {

    private String lastChannel;
    private String lastBody;
    private boolean called = false;

    @Override
    public void send(String channelId, String body) {
        this.lastChannel = channelId;
        this.lastBody = body;
        this.called = true;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastBody() {
        return lastBody;
    }

    public void reset() {
        this.lastChannel = null;
        this.lastBody = null;
        this.called = false;
    }
}
