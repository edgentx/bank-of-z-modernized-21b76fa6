package com.example.mocks;

import com.example.domain.slack.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Captures messages sent to Slack to verify content without real network calls.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String message) {
        // In a real mock, we might record or assert here immediately.
        // For TDD red phase, we just record.
        this.sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public boolean hasReceivedMessageContaining(String substring) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(substring));
    }
}