package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static final String DEFAULT_CHANNEL = "#vforce360-issues";

    public record MessageRequest(String channelId, String body) {}

    private final List<MessageRequest> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channelId, String messageBody) {
        messages.add(new MessageRequest(channelId, messageBody));
    }

    public List<MessageRequest> getMessages() {
        return messages;
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1).body();
    }
}
