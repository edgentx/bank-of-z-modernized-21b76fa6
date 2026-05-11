package com.example.mocks;

import com.example.domain.shared.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub issue creation without calling the actual API.
 */
public class MockGitHubPort implements GitHubPort {
    private final Map<String, String> issues = new HashMap<>();
    private int issueCounter = 1;
    private boolean shouldFail = false;
    private String mockBaseUrl = "https://github.com/example/repo/issues/";
    
    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) return null;
        
        String issueNumber = String.valueOf(issueCounter++);
        String url = mockBaseUrl + issueNumber;
        issues.put(issueNumber, title);
        
        return url;
    }
    
    @Override
    public String getIssueUrl(String issueNumber) {
        if (shouldFail) return null;
        if (!issues.containsKey(issueNumber)) return null;
        
        return mockBaseUrl + issueNumber;
    }
    
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
    
    public void setMockBaseUrl(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }
    
    public int getIssueCount() {
        return issues.size();
    }
    
    public void reset() {
        issues.clear();
        issueCounter = 1;
        shouldFail = false;
    }
}