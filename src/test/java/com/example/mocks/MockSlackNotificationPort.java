package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the Slack notification port for testing.
 * Captures messages sent to Slack for assertion.
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
    public void postMessage(String channel, String messageBody) {
        // In a real test environment, we capture arguments for verification
        this.messages.add(new SentMessage(channel, messageBody));
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
