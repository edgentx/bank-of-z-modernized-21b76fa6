package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> capturedMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // In a real test, we might simulate failures here, but for now we just capture.
        this.capturedMessages.add(messageBody);
    }

    public List<String> getCapturedMessages() {
        return new ArrayList<>(capturedMessages);
    }

    public void clear() {
        capturedMessages.clear();
    }
}
