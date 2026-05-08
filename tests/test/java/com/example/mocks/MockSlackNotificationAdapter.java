package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Stores sent messages in memory for test assertions.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // Store the message in memory instead of making an HTTP call
        this.sentMessages.add(messageBody);
    }

    /**
     * Helper method for tests to retrieve the last sent message body.
     * @return The last sent message body, or null if none sent.
     */
    public String getLastSentBody() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void clear() {
        sentMessages.clear();
    }
}
