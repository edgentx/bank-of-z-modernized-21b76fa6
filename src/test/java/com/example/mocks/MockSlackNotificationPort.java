package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        System.out.println("[MockSlack] Captured message: " + messageBody);
        this.sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void reset() {
        sentMessages.clear();
    }

    public String getLastMessage() {
        if (sentMessages.isEmpty()) {
            throw new IllegalStateException("No messages sent");
        }
        return sentMessages.get(sentMessages.size() - 1);
    }
}