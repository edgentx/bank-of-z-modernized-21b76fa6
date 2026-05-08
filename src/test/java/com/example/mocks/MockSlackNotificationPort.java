package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for verification.
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
    private boolean simulateFailure = false;

    @Override
    public boolean postMessage(String channel, String messageBody) {
        if (simulateFailure) return false;
        this.messages.add(new SentMessage(channel, messageBody));
        return true;
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void reset() {
        messages.clear();
        simulateFailure = false;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    public SentMessage getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }
}
