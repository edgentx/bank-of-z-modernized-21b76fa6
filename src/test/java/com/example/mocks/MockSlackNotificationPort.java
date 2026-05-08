package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages posted to channels to verify behavior without real I/O.
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

    /**
     * Helper to verify if a message was posted to a specific channel.
     */
    public boolean wasPostedTo(String channel) {
        return postedMessages.stream().anyMatch(m -> m.channel.equals(channel));
    }

    /**
     * Helper to find the last message body sent to a specific channel.
     */
    public String getLastBodyForChannel(String channel) {
        return postedMessages.stream()
                .filter(m -> m.channel.equals(channel))
                .reduce((a, b) -> b)
                .map(m -> m.body)
                .orElse(null);
    }
}
