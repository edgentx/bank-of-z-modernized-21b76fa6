package com.example.mocks;

import com.example.ports.IssueTrackerPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of IssueTrackerPort.
 * Returns predictable URLs based on defect ID for testing.
 */
public class MockIssueTracker implements IssueTrackerPort {

    private final Map<String, String> createdIssues = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String createIssue(String defectId, String title) {
        if (shouldFail) {
            throw new RuntimeException("Failed to create issue for " + defectId);
        }
        // Simulate a GitHub URL format
        String url = "https://github.com/mock-repo/issues/" + defectId.hashCode();
        createdIssues.put(defectId, url);
        return url;
    }

    public String getUrlFor(String defectId) {
        return createdIssues.get(defectId);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
