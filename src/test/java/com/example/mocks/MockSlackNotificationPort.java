package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify the content and presence of the GitHub URL.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channelId;
        public final String body;

        public PostedMessage(String channelId, String body) {
            this.channelId = channelId;
            this.body = body;
        }
    }

    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channelId, String body) {
        System.out.println("[MockSlack] Captured message for channel " + channelId + ": " + body);
        this.messages.add(new PostedMessage(channelId, body));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Helper to verify if the GitHub URL was included in the last message body.
     */
    public boolean lastMessageContainsUrl(String expectedUrl) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).body.contains(expectedUrl);
    }
}