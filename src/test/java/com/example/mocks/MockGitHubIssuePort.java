package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.net.URI;

/**
 * Mock Adapter for GitHub Issue Creation.
 * Used in Testing to simulate URLs without calling GitHub APIs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private URI mockUrl;
    private boolean createCalled = false;
    private boolean shouldFail = false;
    
    private String capturedTitle;
    private String capturedDescription;

    public void setMockUrl(URI url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public URI createIssue(String title, String description) {
        this.createCalled = true;
        this.capturedTitle = title;
        this.capturedDescription = description;
        
        if (shouldFail) {
            throw new RuntimeException("Simulated GitHub API Failure");
        }
        
        return mockUrl;
    }

    // Test Inspection Methods
    public boolean wasCreateCalled() {
        return createCalled;
    }

    public String getCapturedTitle() {
        return capturedTitle;
    }

    public String getCapturedDescription() {
        return capturedDescription;
    }
}