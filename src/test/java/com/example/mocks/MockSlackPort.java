package com.example.mocks;

import com.example.ports.SlackPort;
import com.example.domain.validation.model.SlackNotificationMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack for assertion.
 */
public class MockSlackPort implements SlackPort {

    private final List<SlackNotificationMessage> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(SlackNotificationMessage message) {
        // Instead of calling the real API, just store the message
        this.sentMessages.add(message);
    }

    public List<SlackNotificationMessage> getSentMessages() {
        return sentMessages;
    }

    public void reset() {
        sentMessages.clear();
    }
}
