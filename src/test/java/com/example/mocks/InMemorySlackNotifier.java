package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Stores payloads in memory for assertion.
 */
public class InMemorySlackNotifier implements SlackNotifierPort {

    private Map<String, String> lastPayload = new HashMap<>();

    @Override
    public void sendNotification(String message, String githubIssueUrl) {
        // Simulate the construction of the Slack body message
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Message: ").append(message != null ? message : "");
        
        if (githubIssueUrl != null) {
            bodyBuilder.append("\nIssue: ").append(githubIssueUrl);
        }

        this.lastPayload.put("body", bodyBuilder.toString());
        this.lastPayload.put("raw_url", githubIssueUrl);
    }

    /**
     * Retrieves the last payload sent through this mock.
     * Used by tests to verify assertions.
     */
    public Map<String, String> getLastPayload() {
        return this.lastPayload;
    }
}
