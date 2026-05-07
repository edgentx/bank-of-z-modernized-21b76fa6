package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to verify behavior without external I/O.
 */
public class MockSlackPort implements SlackPort {

    public static class PostedMessage {
        public final String channelId;
        public final String body;

        public PostedMessage(String channelId, String body) {
            this.channelId = channelId;
            this.body = body;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channelId, String messageBody) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be null or blank");
        }
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("messageBody cannot be null or blank");
        }
        this.postedMessages.add(new PostedMessage(channelId, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void clear() {
        postedMessages.clear();
    }
}
