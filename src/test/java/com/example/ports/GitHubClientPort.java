package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubClientPort {
    String getIssueUrl(String issueId);
}
