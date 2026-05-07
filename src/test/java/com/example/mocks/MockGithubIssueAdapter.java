package com.example.mocks;

import com.example.ports.GithubIssuePort;
import java.util.UUID;

/**
 * Mock adapter for GithubIssuePort.
 * Simulates GitHub API responses without network calls.
 */
public class MockGithubIssueAdapter implements GithubIssuePort {

    private boolean createCalled = false;
    private String lastTitle;
    private String lastBody;
    private String mockUrl;
    private boolean returnNullUrl = false;

    public MockGithubIssueAdapter() {
        // Generate a stable mock URL
        this.mockUrl = "https://github.com/fake-repo/issues/" + UUID.randomUUID().toString();
    }

    @Override
    public String createIssue(String title, String body) {
        this.createCalled = true;
        this.lastTitle = title;
        this.lastBody = body;
        
        if (returnNullUrl) {
            return null;
        }
        return mockUrl;
    }

    // Test inspection methods
    public boolean wasCreateCalled() {
        return createCalled;
    }

    public String getReturnedUrl() {
        return mockUrl;
    }

    public void setReturnNullUrl(boolean returnNull) {
        this.returnNullUrl = returnNull;
    }
}
