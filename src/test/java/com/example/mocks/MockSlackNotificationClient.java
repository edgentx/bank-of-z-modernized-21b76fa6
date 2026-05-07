package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores messages in memory so they can be inspected by tests.
 */
public class MockSlackNotificationClient implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendDefectNotification(String defectId, String description, String githubIssueUrl) {
        // RED PHASE STUB:
        // Currently, this method does nothing or stores an empty string.
        // This will cause the test shouldContainGitHubIssueUrlInSlackBodyWhenReportingDefect to FAIL,
        // satisfying the TDD Red Phase requirement.
        
        String body = "Defect ID: " + defectId; 
        // Intentionally missing the URL append to simulate the defect state.
        
        sentMessages.add(body);
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
