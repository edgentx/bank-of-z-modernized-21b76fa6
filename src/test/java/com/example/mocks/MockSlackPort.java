package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to verify behavior without real I/O.
 */
public class MockSlackPort implements SlackPort {
    public final List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        this.messages.add(new Message(channel, body));
    }

    public record Message(String channel, String body) {}
}
