package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages sent to verify behavior.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String message;

        public Message(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String message) {
        this.sentMessages.add(new Message(channel, message));
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
