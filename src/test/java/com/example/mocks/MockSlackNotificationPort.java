package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages posted to Slack to verify content and formatting.
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

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        this.postedMessages.add(new PostedMessage(channel, body));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void clear() {
        postedMessages.clear();
    }
}