package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Spy implementation of SlackNotificationPort to capture output for testing.
 * This avoids calling the real Slack API during test runs.
 */
public class SpySlackNotificationAdapter implements SlackNotificationPort {

    private String lastChannel;
    private String lastBody;

    @Override
    public void send(String channel, String body) {
        // Capture state for assertions
        this.lastChannel = channel;
        this.lastBody = body;
        // System.out.println("[SPY] Slack sent to " + channel + ": " + body);
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastBody;
    }

    public void clear() {
        this.lastChannel = null;
        this.lastBody = null;
    }
}
