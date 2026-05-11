package com.example.mocks;

import com.example.infrastructure.defect.SlackNotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackNotifierPort implements SlackNotifierPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // System.out.println("[MockSlack] Received: " + messageBody); // Debugging
        sentMessages.add(messageBody);
    }

    public String getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
