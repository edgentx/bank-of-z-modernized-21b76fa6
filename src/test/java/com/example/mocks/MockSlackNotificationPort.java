package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content and channel.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastChannelId;
    private String lastMessage;
    private boolean shouldFail = false;

    @Override
    public void postMessage(String channelId, String message) {
        if (shouldFail) {
            throw new RuntimeException("Mock Slack Failure");
        }
        this.lastChannelId = channelId;
        this.lastMessage = message;
    }

    // Getters for Assertions
    public String getLastChannelId() { return lastChannelId; }
    public String getLastMessage() { return lastMessage; }

    public void setShouldFail(boolean fail) { this.shouldFail = fail; }
}
