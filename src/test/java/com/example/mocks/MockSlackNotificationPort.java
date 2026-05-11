package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack for verification in tests.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // In a real mock, we might just capture the argument.
        // If we want to simulate a failure, we could throw an exception here.
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}