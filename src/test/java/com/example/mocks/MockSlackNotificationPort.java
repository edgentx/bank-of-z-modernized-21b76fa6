package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages sent to Slack to allow assertions in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public record SentMessage(String channel, String body) {}

    private final List<SentMessage> sentMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean send(String channel, String messageBody) {
        sentMessages.add(new SentMessage(channel, messageBody));
        return shouldSucceed;
    }

    public List<SentMessage> getSentMessages() {
        return sentMessages;
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    public void clear() {
        sentMessages.clear();
    }
}
