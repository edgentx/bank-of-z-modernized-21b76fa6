package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to 'Slack' to verify contents.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("messageBody cannot be null");
        }
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean wasMessageSentContaining(String text) {
        return sentMessages.stream()
            .anyMatch(msg -> msg.contains(text));
    }
    
    public void reset() {
        sentMessages.clear();
    }
}
