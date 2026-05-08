package com.example.mocks;

import com.example.ports.GithubIssuePort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GithubIssuePort for testing.
 * Allows verification that issue creation was requested.
 */
public class MockGithubIssuePort implements GithubIssuePort {

    private final Map<String, String> createdIssues = new HashMap<>();
    private String forcedUrl = "https://github.com/bank-of-z/vforce360/issues/MOCK-123";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        
        // Store request for verification
        createdIssues.put(title, description);
        return forcedUrl;
    }

    public boolean wasIssueCreatedWithTitle(String title) {
        return createdIssues.containsKey(title);
    }

    public String getDescriptionForTitle(String title) {
        return createdIssues.get(title);
    }

    public void setForcedUrl(String url) {
        this.forcedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
    
    public void reset() {
        createdIssues.clear();
    }
}
