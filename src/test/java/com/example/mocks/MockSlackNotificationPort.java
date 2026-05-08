package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }
        this.postedMessages.add(messageBody);
    }

    public List<String> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    public String getLastMessage() {
        if (postedMessages.isEmpty()) {
            throw new IllegalStateException("No messages posted");
        }
        return postedMessages.get(postedMessages.size() - 1);
    }
}
