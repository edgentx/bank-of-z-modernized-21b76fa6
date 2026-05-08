package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates issue creation and returns predictable URLs.
 */
public class MockGitHubAdapter implements GitHubPort {

    private final Map<String, String> stubbedUrls = new HashMap<>();
    private boolean createIssueCalled = false;
    private String lastCreatedTitle;

    /**
     * Stubs a specific Defect ID to return a specific URL.
     */
    public void stubIssueUrl(String defectId, String url) {
        this.stubbedUrls.put(defectId, url);
    }

    @Override
    public String createIssue(String title, String description) {
        this.createIssueCalled = true;
        this.lastCreatedTitle = title;

        // Determine the 'Defect ID' from the title or return a default stub if not configured
        // Real scenario: title might be "VW-454: Validation failed"
        String key = title.split(":")[0].trim(); 
        
        return stubbedUrls.getOrDefault(key, "https://github.com/mock/default-issue");
    }

    // Test Utility Methods

    public boolean wasCreateIssueCalled() {
        return createIssueCalled;
    }

    public String getGeneratedUrl(String defectId) {
        return stubbedUrls.get(defectId);
    }

    public void reset() {
        this.stubbedUrls.clear();
        this.createIssueCalled = false;
        this.lastCreatedTitle = null;
    }
}
