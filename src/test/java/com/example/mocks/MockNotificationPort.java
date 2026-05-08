package com.example.mocks;

import com.example.application.SlackMessage;
import com.example.ports.NotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures messages to verify content without calling Slack.
 */
public class MockNotificationPort implements NotificationPort {

    private final List<SlackMessage> sentMessages = new ArrayList<>();

    @Override
    public void send(SlackMessage message) {
        sentMessages.add(message);
    }

    public List<SlackMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to verify if any sent message contains the specific text.
     */
    public boolean wasTextSent(String text) {
        return sentMessages.stream().anyMatch(msg -> msg.getText().contains(text));
    }
}