package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Stores messages in memory for test verification.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channelId;
        public final String body;

        public SentMessage(String channelId, String body) {
            this.channelId = channelId;
            this.body = body;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        if (simulateFailure) return false;
        messages.add(new SentMessage(channelId, messageBody));
        return true;
    }

    public List<SentMessage> getMessages() {
        return List.copyOf(messages);
    }

    public void clear() {
        messages.clear();
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}