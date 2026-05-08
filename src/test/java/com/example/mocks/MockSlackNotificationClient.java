package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Stores messages in memory for verification during testing.
 */
public class MockSlackNotificationClient implements SlackNotificationPort {

    public record Message(String channel, String body) {}

    private final List<Message> sentMessages = new ArrayList<>();

    @Override
    public boolean sendMessage(String channel, String body) {
        this.sentMessages.add(new Message(channel, body));
        return true;
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}