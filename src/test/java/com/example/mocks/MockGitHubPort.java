package com.example.mocks;

import com.example.ports.GitHubPort;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for GitHub API.
 * Simulates issue creation without calling the real GitHub API.
 */
public class MockGitHubPort implements GitHubPort {

    private URI nextUrl;
    private boolean issueCreated;
    private String lastDescription;
    private URI lastUrl;
    private final List<String> callLog = new ArrayList<>();

    @Override
    public URI createIssue(String title, String description) {
        this.issueCreated = true;
        this.lastDescription = description;
        this.lastUrl = this.nextUrl;
        this.callLog.add("createIssue: " + title);
        
        if (this.lastUrl == null) {
            // Default fallback URL if not explicitly set by test
            return URI.create("https://github.com/mock/default");
        }
        return this.lastUrl;
    }

    // --- Test Helper Methods ---

    /**
     * Configures the mock to return a specific URL on the next 'createIssue' call.
     */
    public void setNextCreateIssueUrl(URI url) {
        this.nextUrl = url;
    }

    public boolean wasCreateIssueCalled() {
        return issueCreated;
    }

    public String getLastCapturedDescription() {
        return lastDescription;
    }

    public URI getLastCreatedUrl() {
        return lastUrl;
    }

    public void reset() {
        this.nextUrl = null;
        this.issueCreated = false;
        this.lastDescription = null;
        this.lastUrl = null;
        this.callLog.clear();
    }
}
