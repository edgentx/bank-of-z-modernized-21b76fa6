package com.example.mocks;

import com.example.ports.IssueTrackerPort;
import java.util.UUID;

/**
 * Mock adapter for IssueTrackerPort.
 * Simulates creating an issue in an external tracker (e.g., GitHub)
 * without performing actual network I/O.
 */
public class InMemoryIssueTracker implements IssueTrackerPort {

    private String lastCreatedUrl;

    @Override
    public String createIssue(String title, String body) {
        // Simulate the ID generation of a real tracker (like GitHub)
        String issueId = UUID.randomUUID().toString().substring(0, 8);
        // Construct a mock URL based on the simulated ID
        this.lastCreatedUrl = "https://github.com/example/bank-of-z/issues/" + issueId;
        return this.lastCreatedUrl;
    }

    public String getLastCreatedUrl() {
        return lastCreatedUrl;
    }
}