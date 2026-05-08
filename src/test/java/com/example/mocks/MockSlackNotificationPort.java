package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<Message> messages = new ArrayList<>();

    public record Message(String channelId, String body) {}

    @Override
    public void sendMessage(String channelId, String messageBody) {
        // In a real mock, we might just store this. 
        // Here we simulate acceptance or simply capture it for assertion.
        this.messages.add(new Message(channelId, messageBody));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
