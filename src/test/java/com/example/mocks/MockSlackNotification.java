package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures sent messages to verify content and channel in assertions.
 */
public class MockSlackNotification implements SlackNotificationPort {

    public final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Simulate basic validation found in real clients
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null or empty");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        
        this.messages.add(new SentMessage(channel, messageBody));
    }

    public record SentMessage(String channel, String body) {}

    public void reset() {
        messages.clear();
    }

    public boolean wasMessageSentTo(String channel) {
        return messages.stream().anyMatch(m -> m.channel().equals(channel));
    }

    public String getLastBodyForChannel(String channel) {
        return messages.stream()
                .filter(m -> m.channel().equals(channel))
                .reduce((a, b) -> b) // get last
                .orElseThrow(() -> new IllegalStateException("No message sent to channel: " + channel))
                .body();
    }
}
