package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to channels to allow verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static final class PostedMessage {
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
        // Simulate validation logic
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null or empty");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        this.postedMessages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    /**
     * Helper method to check if any message posted to a specific channel contains a specific text.
     */
    public boolean channelContains(String channel, String text) {
        return postedMessages.stream()
                .filter(m -> m.channel.equals(channel))
                .anyMatch(m -> m.body.contains(text));
    }
}
