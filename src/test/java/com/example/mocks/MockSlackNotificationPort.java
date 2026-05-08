package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify contents without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    private final List<String> postedBodies = new ArrayList<>();

    @Override
    public void postMessage(String body) {
        if (body == null) {
            throw new IllegalArgumentException("Slack body cannot be null");
        }
        // Simulate recording the message
        this.postedBodies.add(body);
    }

    public List<String> getPostedBodies() {
        return new ArrayList<>(postedBodies);
    }

    public void clear() {
        postedBodies.clear();
    }
}