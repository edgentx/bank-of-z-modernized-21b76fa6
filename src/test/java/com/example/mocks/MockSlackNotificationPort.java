package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to 'Slack' to verify content without external I/O.
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

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        System.out.println("[MockSlack] Sending to " + channel + ": " + messageBody);
        this.messages.add(new SentMessage(channel, messageBody));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public SentMessage getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }
}
