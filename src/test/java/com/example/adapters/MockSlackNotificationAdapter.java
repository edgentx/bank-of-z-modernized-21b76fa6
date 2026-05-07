package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notifications.
 * Keeps a log of posted messages for assertion in tests.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    public static class Message {
        public final String channel;
        public final String content;

        public Message(String channel, String content) {
            this.channel = channel;
            this.content = content;
        }
    }

    private final List<Message> messages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean postMessage(String channel, String message) {
        if (simulateFailure) {
            return false;
        }
        messages.add(new Message(channel, message));
        return true;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
