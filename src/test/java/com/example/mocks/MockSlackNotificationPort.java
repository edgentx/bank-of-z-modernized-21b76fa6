package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last sent body to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastChannel;
    private String lastBody;
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        return !shouldFail;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastBody() {
        return lastBody;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}
