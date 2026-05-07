package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(String channelId, String message) {
        messages.add(new Message(channelId, message));
    }

    public record Message(String channelId, String content) {}

    public boolean contains(String substring) {
        return messages.stream().anyMatch(m -> m.content.contains(substring));
    }
}
