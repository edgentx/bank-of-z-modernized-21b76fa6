package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory instead of calling the real Slack API.
 */
@Component
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final Map<String, String> sentMessages = new HashMap<>();

    @Override
    public void sendNotification(String channel, String messageBody) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null/blank");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be null/blank");
        }
        sentMessages.put(channel, messageBody);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return sentMessages.get(channel);
    }

    public void clear() {
        sentMessages.clear();
    }
}