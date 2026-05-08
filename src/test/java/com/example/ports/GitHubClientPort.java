package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Implemented by adapters (e.g., real GitHub API client or test mock).
 */
public interface GitHubClientPort {
    String getIssueUrl(String issueId);
}
