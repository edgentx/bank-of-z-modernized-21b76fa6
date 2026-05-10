package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages instead of sending them.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        sentMessages.add(messageBody);
        System.out.println("[MOCK SLACK] Sending: " + messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean wasCalledWith(String snippet) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(snippet));
    }

    public void clear() {
        sentMessages.clear();
    }
}