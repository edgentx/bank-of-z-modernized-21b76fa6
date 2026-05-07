package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent during workflow execution to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // In a real mock/test double, we just capture the argument.
        // We avoid throwing exceptions here to keep workflow logic clean unless testing error paths.
        this.sentMessages.add(messageBody);
    }

    /**
     * Retrieves the last message sent.
     */
    public String getLastMessage() {
        if (sentMessages.isEmpty()) {
            throw new IllegalStateException("No messages were sent");
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    /**
     * Clears the message history (useful between test cases).
     */
    public void clear() {
        sentMessages.clear();
    }

    /**
     * Asserts that at least one message sent contains the specific fragment.
     */
    public boolean assertMessageContains(String fragment) {
        return sentMessages.stream()
                .anyMatch(msg -> msg != null && msg.contains(fragment));
    }
}