package com.example.steps;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Used in TDD Red phase to verify output without hitting the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // Simulate the external call by storing the payload
        this.sentBodies.add(messageBody);
        // In a real mock framework, we might throw exceptions here to test error paths,
        // but for the Red phase we just capture the state.
    }

    public String getLastBody() {
        if (sentBodies.isEmpty()) {
            return null;
        }
        return sentBodies.get(sentBodies.size() - 1);
    }

    public void clear() {
        sentBodies.clear();
    }
}
