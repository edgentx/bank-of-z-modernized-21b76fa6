package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendDefectReport(String messageBody) {
        // In a real scenario, this would use the Slack WebClient.
        // Here we just capture the body for verification.
        sentMessages.add(messageBody);
    }

    public void clear() {
        sentMessages.clear();
    }
}
