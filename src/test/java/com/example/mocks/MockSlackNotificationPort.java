package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Captures messages to verify content during tests without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        messages.add(new PostedMessage(channel, body));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
