package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Stores messages in memory for verification.
 */
@Component
public class MockSlackClient implements SlackPort {

    private final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendMessage(String channel, String body) {
        // In a real mock, we might just store this.
        // This simulates the side-effect of sending.
        this.messages.add(new Message(channel, body));
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1).body();
    }

    public String getLastChannel() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1).channel();
    }

    public void clear() {
        messages.clear();
    }
}