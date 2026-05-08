package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Allows verification of the body content without a real network call.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> publishedBodies = new ArrayList<>();
    private boolean shouldSucceed = true;
    private String mockUrlBase = "http://github.com/mock/issues/";

    public void setMockUrlBase(String url) {
        this.mockUrlBase = url;
    }

    public void setShouldSucceed(boolean succeed) {
        this.shouldSucceed = succeed;
    }

    public List<String> getPublishedBodies() {
        return new ArrayList<>(publishedBodies);
    }

    @Override
    public NotificationResult publishDefect(String channel, String title, String defectId) {
        if (!shouldSucceed) {
            return new NotificationResult(false, null);
        }

        // Simulate the body generation that the real worker would do
        // This mimics the 'Actual Behavior' state where we verify the link
        String body = String.format("Defect Reported: %s - ID: %s - GitHub: %s%s", title, defectId, mockUrlBase, defectId);
        
        publishedBodies.add(body);
        return new NotificationResult(true, body);
    }
}
