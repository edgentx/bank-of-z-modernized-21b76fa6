package com.example.mocks;

import com.example.domain.vforce.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean sendDefectReport(String messageBody) {
        if (simulateFailure) {
            return false;
        }
        sentMessages.add(messageBody);
        return true;
    }

    /**
     * Retrieves the list of messages sent during the test.
     */
    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    /**
     * Clears the message history.
     */
    public void clear() {
        sentMessages.clear();
    }

    /**
     * Configures the mock to simulate a send failure.
     */
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
