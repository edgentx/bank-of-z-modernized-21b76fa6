package com.example.mocks;

import com.example.domain.shared.slack.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages posted during the test lifecycle for assertion.
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
    public boolean postMessage(String channel, String body) {
        // Simulate basic validation logic found in real adapters
        if (channel == null || body == null) return false;
        messages.add(new PostedMessage(channel, body));
        return true;
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public boolean containsUrl(String url) {
        return messages.stream().anyMatch(msg -> msg.body.contains(url));
    }
}