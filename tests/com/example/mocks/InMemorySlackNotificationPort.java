package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores the last sent message to allow assertions in tests.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private boolean called = false;
    private String lastChannel;
    private String lastMessageBody;

    @Override
    public boolean postMessage(String channel, String messageBody) {
        this.called = true;
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        // In a mock, we assume success unless configured otherwise
        return true;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }
}