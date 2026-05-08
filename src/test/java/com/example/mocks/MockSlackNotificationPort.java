package com.example.mocks;

import com.example.domain.vforce360.ports.VForce360NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for VForce360 Slack notifications.
 * Captures messages for verification in tests.
 */
public class MockSlackNotificationPort implements VForce360NotificationPort {

    private final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        // Validate input as the real implementation might
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

    /**
     * Helper to verify if any message contains the GitHub URL.
     */
    public boolean wasUrlPosted(String url) {
        return postedMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
