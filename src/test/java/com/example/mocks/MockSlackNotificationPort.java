package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records sent messages instead of actually calling Slack.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public record SentMessage(String channelId, String body) {}

    private final List<SentMessage> sentMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        sentMessages.add(new SentMessage(channelId, messageBody));
        return shouldSucceed;
    }

    public List<SentMessage> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
}