package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures messages sent to channels to verify content in assertions.
 */
public class MockNotificationAdapter implements NotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String body;

        public SentMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void send(String channel, String body) {
        System.out.println("[MockNotification] Sending to " + channel + ": " + body);
        this.messages.add(new SentMessage(channel, body));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
