package com.example.mocks;

import com.example.domain.ports.SlackNotificationPort;

import java.util.concurrent.CompletableFuture;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the message body sent to Slack for assertion.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastReceivedBody;
    private boolean shouldFail = false;

    @Override
    public CompletableFuture<String> send(String messageBody) {
        this.lastReceivedBody = messageBody;
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("Slack API unavailable"));
        }
        // Return a fake timestamp
        return CompletableFuture.completedFuture("1234567890.123456");
    }

    public String getLastReceivedBody() {
        return lastReceivedBody;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}