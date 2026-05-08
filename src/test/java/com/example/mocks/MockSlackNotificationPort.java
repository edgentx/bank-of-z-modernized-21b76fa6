package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages in memory to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        System.out.println("[MockSlack] Captured Message: " + messageBody);
        this.sentMessages.add(messageBody);
    }

    @Override
    public String getLastSentMessageBody() {
        if (sentMessages.isEmpty()) {
            return "";
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    /**
     * Clears the message history. Useful for test isolation.
     */
    public void clear() {
        sentMessages.clear();
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }
}
