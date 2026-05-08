package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Records sent messages to verify content in tests without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        // Prevent null payloads which might cause NPEs in the real implementation
        if (messageBody == null) {
            throw new IllegalArgumentException("Slack message body cannot be null");
        }
        this.sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
