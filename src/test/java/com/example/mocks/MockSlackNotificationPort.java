package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public static class Message {
        public final String channel;
        public final String text;

        public Message(String channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void sendText(String channel, String text) {
        this.sentMessages.add(new Message(channel, text));
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public Message getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }
}
