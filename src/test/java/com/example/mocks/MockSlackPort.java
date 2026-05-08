package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation for Slack.
 * Stores messages in memory for verification during tests.
 */
@Component
public class MockSlackPort implements SlackPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendMessage(String body) {
        // Simulate sending a message
        this.sentBodies.add(body);
    }

    /**
     * Helper method for assertions.
     */
    public String getLastMessageBody() {
        if (sentBodies.isEmpty()) {
            return null;
        }
        return sentBodies.get(sentBodies.size() - 1);
    }

    public void clear() {
        sentBodies.clear();
    }
}
