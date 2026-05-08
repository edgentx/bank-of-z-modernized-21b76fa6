package com.example.mocks;

import com.example.adapters.slack.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Records messages to memory instead of making HTTP calls.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // Simulate network latency logic if needed, but for now just record
        this.sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
