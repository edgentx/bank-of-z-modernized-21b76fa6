package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Simulates creating an issue and returning a URL.
 */
public class InMemoryGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> issueUrls = new HashMap<>();

    @Override
    public String createIssue(String defectId, String title, String body) {
        // Simulate GitHub URL generation
        String url = "https://github.com/example/project/issues/" + defectId;
        issueUrls.put(defectId, url);
        return url;
    }

    public boolean wasIssueCreated(String defectId) {
        return issueUrls.containsKey(defectId);
    }

    public String getIssueUrl(String defectId) {
        return issueUrls.get(defectId);
    }
}
