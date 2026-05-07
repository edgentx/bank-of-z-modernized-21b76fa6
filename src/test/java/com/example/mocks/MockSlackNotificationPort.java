package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages in memory for verification during tests.
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
    public void postMessage(String channelId, String messageBody) {
        // Store in memory instead of calling real API
        this.messages.add(new PostedMessage(channelId, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Helper assertion to check if the last message contained the specific GitHub URL.
     */
    public boolean lastMessageContainsUrl(String url) {
        if (messages.isEmpty()) return false;
        PostedMessage last = messages.get(messages.size() - 1);
        return last.body.contains(url);
    }
}