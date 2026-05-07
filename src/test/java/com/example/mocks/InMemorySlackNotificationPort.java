package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of {@link SlackNotificationPort} for testing.
 * Captures messages sent to Slack for assertion.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean send(String message) {
        if (simulateFailure) {
            return false;
        }
        sentMessages.add(message);
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
