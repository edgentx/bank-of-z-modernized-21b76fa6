package com.example.mocks;

import com.example.infrastructure.slack.SlackNotifier;

/**
 * Mock implementation of SlackNotifier for testing.
 * Records messages instead of sending real HTTP requests.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastMessage;
    private boolean failNextCall = false;

    @Override
    public void send(String message) {
        if (failNextCall) {
            throw new RuntimeException("Simulated Slack failure");
        }
        this.lastMessage = message;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setFailNextCall(boolean fail) {
        this.failNextCall = fail;
    }
}