package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MockGitHubAdapter implements GitHubPort {
    
    private final Map<String, String> issueUrls = new ConcurrentHashMap<>();
    private final Map<String, String> issueTitles = new ConcurrentHashMap<>();
    private String baseUrl = "https://github.com/example-org/example-repo/issues/";
    
    @Override
    public String createIssue(String defectId, String title) {
        if (defectId == null || defectId.isEmpty()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        
        // Simulate creating an issue and generating a URL
        String issueUrl = baseUrl + defectId;
        issueUrls.put(defectId, issueUrl);
        issueTitles.put(defectId, title);
        return issueUrl;
    }
    
    @Override
    public Optional<String> getIssueUrl(String defectId) {
        return Optional.ofNullable(issueUrls.get(defectId));
    }
    
    @Override
    public void setupIssueCreation(String defectId, String title) {
        // Pre-configure the mock to have this issue ready
        createIssue(defectId, title);
    }
    
    /**
     * Set a custom base URL for testing
     */
    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
    
    /**
     * Clear all stored issues (useful between tests)
     */
    public void clear() {
        issueUrls.clear();
        issueTitles.clear();
    }
    
    /**
     * Check if an issue exists for the given defect ID
     */
    public boolean hasIssue(String defectId) {
        return issueUrls.containsKey(defectId);
    }
}