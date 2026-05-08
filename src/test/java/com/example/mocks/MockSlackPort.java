package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to verify content and formatting.
 */
public class MockSlackPort implements SlackPort {

    private final List<Message> messages = new ArrayList<>();
    private String mockEndpoint = "https://slack.com/api/mock";

    public record Message(String channel, String body) {}

    @Override
    public CompletableFuture<Void> sendMessage(String channel, String messageBody) {
        // Simulate async behavior
        messages.add(new Message(channel, messageBody));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getEndpointUrl() {
        return mockEndpoint;
    }

    public void setMockEndpoint(String url) {
        this.mockEndpoint = url;
    }

    // Assertions helpers

    public boolean hasReceivedMessageForChannel(String channel) {
        return messages.stream().anyMatch(m -> m.channel().equals(channel));
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) throw new IllegalStateException("No messages received");
        return messages.get(messages.size() - 1).body();
    }

    public String getLastMessageChannel() {
        if (messages.isEmpty()) throw new IllegalStateException("No messages received");
        return messages.get(messages.size() - 1).channel();
    }

    public void clear() {
        messages.clear();
    }
}
