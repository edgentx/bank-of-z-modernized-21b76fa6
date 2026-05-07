package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent during the test execution for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean failNextSend = false;

    @Override
    public void sendNotification(String message) {
        if (failNextSend) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Cannot send blank message");
        }
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setFailNextSend(boolean fail) {
        this.failNextSend = fail;
    }
}
