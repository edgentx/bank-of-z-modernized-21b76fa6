package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures posted messages to validate content (VW-454).
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
        messages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    /**
     * Helper for VW-454 validation: checks if the last message contains the URL.
     */
    public boolean lastMessageContainsUrl(String url) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).body.contains(url);
    }

    /**
     * Helper for VW-454 validation: checks if the last message was for the specific channel.
     */
    public boolean lastMessageWasToChannel(String channel) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).channel.equals(channel);
    }
}
