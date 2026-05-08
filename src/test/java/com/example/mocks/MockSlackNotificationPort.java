package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages posted to Slack to verify content without external I/O.
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
    public void postMessage(String channel, String messageBody) {
        if (channel == null || messageBody == null) {
            throw new IllegalArgumentException("Channel and body must not be null");
        }
        this.postedMessages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void reset() {
        postedMessages.clear();
    }

    public PostedMessage getSingleMessage() {
        if (postedMessages.size() != 1) {
            throw new IllegalStateException("Expected exactly one message, but found " + postedMessages.size());
        }
        return postedMessages.get(0);
    }
}
