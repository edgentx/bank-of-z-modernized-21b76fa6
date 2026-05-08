package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages posted to Slack for verification in tests.
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
    public void postMessage(String channel, String messageBody) {
        this.messages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
