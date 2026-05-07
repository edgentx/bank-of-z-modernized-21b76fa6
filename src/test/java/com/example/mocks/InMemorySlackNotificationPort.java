package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify their content.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final List<String> notifications = new ArrayList<>();

    @Override
    public void sendNotification(String defectId, String summary, String githubUrl) {
        // Simulate the formatting of the Slack message body.
        // This mock helps verify that the URL is actually passed into the body generation logic.
        String body = String.format(
            "Defect Reported: %s\nSummary: %s\nGitHub Issue: %s", 
            defectId, summary, githubUrl
        );
        notifications.add(body);
    }

    public boolean wasNotificationSent() {
        return !notifications.isEmpty();
    }

    public String getLastNotificationBody() {
        if (notifications.isEmpty()) {
            return null;
        }
        return notifications.get(notifications.size() - 1);
    }

    public void clear() {
        notifications.clear();
    }
}
