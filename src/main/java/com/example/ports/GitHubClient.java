package com.example.ports;

import java.util.List;

/**
 * Port interface for creating issues in GitHub.
 * Used by the Validation Aggregate to externalize side effects.
 */
public interface GitHubClient {
    
    /**
     * Creates a GitHub issue.
     * @param title The issue title.
     * @param body The issue body (description).
     * @param labels Labels to apply.
     * @return The HTML URL of the created issue (e.g., https://github.com/org/repo/issues/123).
     */
    String createIssue(String title, String body, List<String> labels);
}
