package com.example.mocks;

import com.example.domain.ports.SlackNotifier;

/**
 * Mock adapter for Slack Notifier.
 * Captures the message body for assertions in tests.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastBody;
    private int callCount = 0;

    @Override
    public void notify(String messageBody) {
        this.lastBody = messageBody;
        this.callCount++;
    }

    public String getLastBody() {
        return lastBody;
    }

    public boolean wasCalled() {
        return callCount > 0;
    }

    public int getCallCount() {
        return callCount;
    }
}
