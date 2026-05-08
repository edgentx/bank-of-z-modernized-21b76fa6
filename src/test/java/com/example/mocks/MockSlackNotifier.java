package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort.
 * Captures messages for verification in tests.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        // Simulate latency or logic if necessary
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        this.postedMessages.add(messageBody);
    }

    public List<String> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void reset() {
        postedMessages.clear();
    }
}
