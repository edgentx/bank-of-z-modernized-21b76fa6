package com.example.mocks;

import com.example.ports.NotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotifierPort.
 * Captures messages in memory for verification in tests.
 */
public class MockNotifier implements NotifierPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String body) {
        // Store the message instead of performing a real HTTP call
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        messages.add(body);
    }

    /**
     * Retrieves the last message sent.
     * @return The last message string, or null if no messages sent.
     */
    public String getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    /**
     * Retrieves all messages sent to this mock.
     * @return List of message strings.
     */
    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * Clears the message history. Useful between test cases.
     */
    public void clear() {
        messages.clear();
    }
}