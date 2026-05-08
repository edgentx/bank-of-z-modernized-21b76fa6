package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for Slack notifications.
 * Captures the message body for verification by the test framework.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private String lastMessageBody;
    private String lastChannel;
    private int callCount = 0;

    @Override
    public boolean postMessage(String channel, String messageBody) {
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        this.callCount++;
        // Simulate success
        return true;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public int getCallCount() {
        return callCount;
    }
}
