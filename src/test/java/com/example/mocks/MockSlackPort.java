package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent during test execution.
 */
public class MockSlackPort implements SlackPort {

    public final List<Message> sentMessages = new ArrayList<>();

    public record Message(String channelId, String body) {}

    @Override
    public void sendMessage(String channelId, String messageBody) {
        this.sentMessages.add(new Message(channelId, messageBody));
    }

    public void reset() {
        sentMessages.clear();
    }
}
