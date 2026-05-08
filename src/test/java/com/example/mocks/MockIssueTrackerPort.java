package com.example.mocks;

import com.example.ports.IssueTrackerPort;

/**
 * Mock adapter for Issue Tracker.
 * Returns deterministic URLs based on input IDs.
 */
public class MockIssueTrackerPort implements IssueTrackerPort {

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return "https://github.com/example/bank-of-z/issues/unknown";
        }
        // Return a deterministic URL for the given ID
        return "https://github.com/example/bank-of-z/issues/" + issueId;
    }
}
