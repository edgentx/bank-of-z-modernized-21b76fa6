package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotificationPort.
 * Records messages instead of sending real HTTP requests.
 */
public class MockSlackNotificationService implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        // Simulate potential failure if message is null
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        this.messages.add(messageBody);
    }

    /**
     * Resets the internal state of the mock.
     */
    public void reset() {
        messages.clear();
    }

    /**
     * Retrieves the body of the last message sent.
     */
    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    /**
     * Retrieves all messages sent during the test.
     */
    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
