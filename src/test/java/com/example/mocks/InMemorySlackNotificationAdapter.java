package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the Slack Notification Port.
 * Stores messages in memory for test verification.
 */
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();
    private boolean failOnSend = false;

    @Override
    public void sendNotification(String messageBody) {
        if (failOnSend) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        System.out.println("[MockSlack] Sending: " + messageBody);
        this.messages.add(messageBody);
    }

    // Test Helper Methods

    public boolean wasNotificationSent() {
        return !messages.isEmpty();
    }

    public String getLastNotificationBody() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void reset() {
        messages.clear();
    }

    public void setFailOnSend(boolean fail) {
        this.failOnSend = fail;
    }
}
