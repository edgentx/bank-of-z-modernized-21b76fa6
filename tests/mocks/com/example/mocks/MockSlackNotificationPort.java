package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory to allow assertions on the content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean send(String messageBody) {
        if (simulateFailure) {
            return false;
        }
        sentMessages.add(messageBody);
        return true;
    }

    /**
     * Retrieves the body of the last sent message.
     * Useful for assertions in unit tests.
     */
    public String getLastMessageBody() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    /**
     * Retrieves all messages sent via this mock.
     */
    public List<String> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }

    /**
     * Resets the mock state.
     */
    public void clear() {
        sentMessages.clear();
        simulateFailure = false;
    }

    /**
     * Configures the mock to simulate a transmission failure.
     */
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
