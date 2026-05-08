package com.example.mocks;

import com.example.ports.SlackPublisher;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPublisher for testing.
 * Captures published messages to verify content without external I/O.
 */
public class MockSlackPublisher implements SlackPublisher {

    public static class PublishedMessage {
        public final String channel;
        public final String body;

        public PublishedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PublishedMessage> messages = new ArrayList<>();

    @Override
    public void publish(String channel, String body) {
        // Capture the call for assertions
        this.messages.add(new PublishedMessage(channel, body));
    }

    public List<PublishedMessage> getMessages() {
        return messages;
    }

    public void reset() {
        messages.clear();
    }
}
