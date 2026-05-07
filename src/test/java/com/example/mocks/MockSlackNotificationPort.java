package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotificationPort.
 * Captures messages sent to Slack to verify behavior in tests without I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String message;

        public SentMessage(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<SentMessage> sentMessages = new ArrayList<>();

    @Override
    public void send(String channel, String message) {
        this.sentMessages.add(new SentMessage(channel, message));
    }

    public List<SentMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to assert the last message contained a specific string.
     */
    public boolean lastMessageContains(String substring) {
        if (sentMessages.isEmpty()) return false;
        String lastMsg = sentMessages.get(sentMessages.size() - 1).message;
        return lastMsg != null && lastMsg.contains(substring);
    }
}