package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to memory for assertion.
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
        System.out.println("[MockSlack] Sending to " + channelId + ": " + messageBody);
        this.messages.add(new PostedMessage(channelId, messageBody));
    }

    public List<PostedMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
