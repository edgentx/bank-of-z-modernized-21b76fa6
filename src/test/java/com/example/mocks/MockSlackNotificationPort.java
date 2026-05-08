package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores sent messages in memory for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<SentMessage> messages = new ArrayList<>();
    private boolean shouldFail = false;

    public record SentMessage(String channel, String body) {}

    @Override
    public String sendMessage(String channel, String messageBody) {
        if (shouldFail) {
            return null;
        }
        // Simulate Slack API returning a timestamp
        String timestamp = "" + System.currentTimeMillis();
        messages.add(new SentMessage(channel, messageBody));
        return timestamp;
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void reset() {
        messages.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}