package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Used in testing to capture outgoing payloads without calling the real Slack API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String body;

        public SentMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SentMessage> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String messageBody) {
        // Capture the call
        this.sentMessages.add(new SentMessage(channel, messageBody));
    }

    public List<SentMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}