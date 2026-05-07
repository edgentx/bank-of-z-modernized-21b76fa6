package com.example.domain.defect.ports;

/**
 * Port for interacting with GitHub issue tracking.
 * Part of the Mock Adapter pattern.
 */
public interface GitHubIssueTracker {
    
    /**
     * Creates an issue in the tracker.
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The full URL of the created issue.
     */
    String createIssue(String title, String body);
    
    /**
     * A simplified exception for GitHub failures.
     */
    class GitHubException extends RuntimeException {
        public GitHubException(String message) {
            super(message);
        }
    }
}
