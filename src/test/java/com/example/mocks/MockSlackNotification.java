package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for verification.
 */
public class MockSlackNotification implements SlackNotificationPort {

    public record Message(String channel, String body) {}

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String body) {
        this.sentMessages.add(new Message(channel, body));
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    /**
     * Helper to verify if a specific URL was sent to a specific channel.
     */
    public boolean wasUrlSentToChannel(String url, String channel) {
        return sentMessages.stream()
            .filter(m -> m.channel().equals(channel))
            .anyMatch(m -> m.body().contains(url));
    }
}
