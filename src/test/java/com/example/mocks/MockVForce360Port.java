package com.example.mocks;

import com.example.ports.VForce360Port;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of VForce360Port for testing.
 * Simulates the creation of GitHub issues and tracks requests.
 */
public class MockVForce360Port implements VForce360Port {

    private final Map<String, String> reportedDefects = new HashMap<>(); // Key: title, Value: URL
    private boolean shouldFail = false;
    private String simulatedGitHubBaseUrl = "https://github.com/bank-of-z/issues/";
    private int issueCounter = 1;

    @Override
    public String reportDefect(String projectId, String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("Simulated VForce360 outage");
        }
        
        // Simulate creating a GitHub issue
        String issueUrl = simulatedGitHubBaseUrl + issueCounter++;
        reportedDefects.put(title, issueUrl);
        return issueUrl;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public int getReportCount() {
        return reportedDefects.size();
    }

    public boolean wasReported(String title) {
        return reportedDefects.containsKey(title);
    }
}
