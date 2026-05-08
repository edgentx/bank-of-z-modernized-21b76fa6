package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without calling the real API.
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
    private boolean shouldThrowException = false;

    @Override
    public void postMessage(String channel, String messageBody) {
        if (shouldThrowException) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        postedMessages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void reset() {
        postedMessages.clear();
        shouldThrowException = false;
    }

    public void setShouldThrowException(boolean value) {
        this.shouldThrowException = value;
    }

    /**
     * Helper to assert that a specific URL was posted to a specific channel.
     */
    public boolean verifyUrlPosted(String channel, String url) {
        return postedMessages.stream()
                .filter(m -> m.channel.equals(channel))
                .anyMatch(m -> m.body.contains(url));
    }
}
