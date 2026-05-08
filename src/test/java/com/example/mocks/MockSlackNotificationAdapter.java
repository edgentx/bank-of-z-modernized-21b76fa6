package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory instead of sending real HTTP requests.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // In a real implementation, this might use WebClient.post()...
        // Here we just capture the output for verification.
        if (messageBody == null) {
            throw new IllegalArgumentException("messageBody cannot be null");
        }
        this.postedMessages.add(messageBody);
    }

    /**
     * Helper method for tests to retrieve the last sent message.
     * @return The body of the most recent notification, or null if none sent.
     */
    public String getLatestPostedBody() {
        if (postedMessages.isEmpty()) {
            return null;
        }
        return postedMessages.get(postedMessages.size() - 1);
    }

    public void clear() {
        postedMessages.clear();
    }
}
