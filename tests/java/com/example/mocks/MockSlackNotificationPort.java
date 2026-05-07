package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Stores sent messages in memory for test verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<SentMessage> sentMessages = new ArrayList<>();

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        sentMessages.add(new SentMessage(channelId, messageBody));
        return true;
    }

    public record SentMessage(String channelId, String messageBody) {}
}
