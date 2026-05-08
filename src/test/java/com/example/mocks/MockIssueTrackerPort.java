package com.example.mocks;

import com.example.ports.IssueTrackerPort;

/**
 * Mock implementation of IssueTrackerPort for testing.
 * Returns predictable URLs without calling external APIs.
 */
public class MockIssueTrackerPort implements IssueTrackerPort {

    private static final String MOCK_BASE_URL = "https://github.com/mock-repo/issues/";
    private int issueCounter = 100;

    @Override
    public String createIssue(String title, String description) {
        String issueUrl = MOCK_BASE_URL + issueCounter++;
        // In a real test scenario, we might want to assert on title/desc content here
        return issueUrl;
    }

    public void reset() {
        issueCounter = 100;
    }
}
