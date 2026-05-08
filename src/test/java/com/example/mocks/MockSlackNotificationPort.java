package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing purposes.
 * Captures messages sent to Slack to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postMessage(String messageBody) {
        if (shouldFail) {
            return false;
        }
        postedMessages.add(messageBody);
        return true;
    }

    /**
     * Retrieves the list of messages sent during the test.
     */
    public List<String> getPostedMessages() {
        return postedMessages;
    }

    /**
     * Utility method to simulate Slack API failure.
     */
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    /**
     * Clears the message history. Useful for test isolation.
     */
    public void clear() {
        postedMessages.clear();
    }
}