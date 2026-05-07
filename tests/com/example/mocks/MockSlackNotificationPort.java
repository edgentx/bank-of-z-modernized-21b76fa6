package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without actual I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String body;

        public Message(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public boolean sendMessage(String channel, String body) {
        // Store the message for verification in tests
        sentMessages.add(new Message(channel, body));
        return true;
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to find the most recent message sent to a specific channel.
     */
    public Message getLastMessageForChannel(String channel) {
        for (int i = sentMessages.size() - 1; i >= 0; i--) {
            if (sentMessages.get(i).channel.equals(channel)) {
                return sentMessages.get(i);
            }
        }
        return null;
    }
}
