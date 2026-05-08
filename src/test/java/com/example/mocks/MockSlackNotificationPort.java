package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent messages to verify content without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean sendNotification(String messageBody) {
        if (simulateFailure) {
            return false;
        }
        sentMessages.add(messageBody);
        return true;
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
