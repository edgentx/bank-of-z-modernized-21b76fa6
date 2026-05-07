package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Stores messages in memory for verification in tests.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendDefectNotification(String defectId, String message, URI githubUrl) {
        // Simulate the construction of the Slack message body.
        // This logic mimics what the real implementation might do.
        // The bug (VW-454) implies that githubUrl might be missing here.
        
        String body = String.format(
            "Defect Report: %s\nMessage: %s\nGitHub Issue: %s", 
            defectId, 
            message, 
            (githubUrl != null ? githubUrl.toString() : "PENDING")
        );
        
        sentBodies.add(body);
        
        // In a real scenario, this would do an HTTP POST.
        // System.out.println("[Mock Slack] Sent: " + body);
    }

    public String getLastSentBody() {
        if (sentBodies.isEmpty()) {
            throw new IllegalStateException("No messages sent");
        }
        return sentBodies.get(sentBodies.size() - 1);
    }

    public void clear() {
        sentBodies.clear();
    }
}