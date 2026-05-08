package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification Port.
 * Captures messages in memory for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> sentMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    @Override
    public boolean sendNotification(String messageBody) {
        if (shouldSucceed) {
            sentMessages.add(messageBody);
            return true;
        }
        return false;
    }

    public boolean wasCalledWith(String substring) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(substring));
    }
}
