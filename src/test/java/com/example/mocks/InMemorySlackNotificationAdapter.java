package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Stores messages in memory instead of calling the real Slack API.
 */
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendNotification(String githubUrl, String title) {
        // This is a placeholder implementation that simulates the 'Actual Behavior'.
        // We deliberately format the body INCORRECTLY here to simulate the defect (VW-454)
        // before the fix is applied. This ensures the test FAILS (Red Phase) initially.
        //
        // Current Behavior (Defect): The body includes the title but NOT the URL.
        String body = "New Defect Reported: " + title; 
        
        // Note: The 'githubUrl' parameter is ignored in this mock implementation
        // to simulate the bug where the link is missing from the body.
        
        sentBodies.add(body);
    }

    /**
     * Helper method for test assertions to retrieve the last sent body.
     */
    public String getLastBodySent() {
        if (sentBodies.isEmpty()) {
            return null;
        }
        return sentBodies.get(sentBodies.size() - 1);
    }
}
