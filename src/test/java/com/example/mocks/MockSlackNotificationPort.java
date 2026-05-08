package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void send(String body) {
        sentBodies.add(body);
    }

    public List<String> getSentBodies() {
        return new ArrayList<>(sentBodies);
    }

    public boolean wasCalledWith(String body) {
        return sentBodies.contains(body);
    }

    public boolean containsMessage(String substring) {
        return sentBodies.stream().anyMatch(body -> body.contains(substring));
    }

    public int getCallCount() {
        return sentBodies.size();
    }
}
