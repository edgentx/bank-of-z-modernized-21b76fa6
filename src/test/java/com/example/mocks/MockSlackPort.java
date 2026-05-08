package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackPort.
 * Captures messages to verify the content (specifically the GitHub URL) without I/O.
 */
public class MockSlackPort implements SlackPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // Capture the message for assertion
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        sentMessages.add(messageBody);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
