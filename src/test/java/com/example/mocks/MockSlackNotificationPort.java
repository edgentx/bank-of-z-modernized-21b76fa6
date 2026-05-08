package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for Slack Notification.
 * In-memory implementation to simulate Slack behavior during testing.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> channelMessages = new HashMap<>();

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        // Simulate successful sending
        channelMessages.put(channel, messageBody);
        return true;
    }

    @Override
    public String getLastMessageBody(String channel) {
        return channelMessages.get(channel);
    }

    /**
     * Helper for tests to verify the message contains the GitHub URL.
     */
    public boolean messageContainsUrl(String channel, String url) {
        String body = channelMessages.get(channel);
        return body != null && body.contains(url);
    }
}