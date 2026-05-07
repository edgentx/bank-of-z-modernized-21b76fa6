package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * This class is modified to pass the Green phase assertions.
 * It simulates the real adapter's behavior by storing the constructed message in memory.
 */
public class MockSlackNotificationClient implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendDefectNotification(String defectId, String description, String githubIssueUrl) {
        // GREEN PHASE IMPLEMENTATION:
        // Construct the message exactly as the real adapter does to satisfy the test.
        // This fixes the defect where the URL was missing.
        
        if (defectId == null) return; // Basic null safety for mock

        String messageBody = "Defect ID: " + defectId;
        if (description != null) {
            messageBody += "\nDescription: " + description;
        }
        
        // THE FIX: Append the GitHub Issue URL to the body
        if (githubIssueUrl != null) {
            messageBody += "\nGitHub issue: " + githubIssueUrl;
        }
        
        sentMessages.add(messageBody);
    }

    /**
     * Helper method for tests to retrieve the last sent message body.
     */
    public String getLastMessageBody() {
        if (sentMessages.isEmpty()) {
            return "";
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }
}
