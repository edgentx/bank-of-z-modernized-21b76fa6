package com.example.ports;

/**
 * Port interface for retrieving GitHub issue metadata.
 */
public interface GitHubPort {
    String getIssueUrl(String issueId);
}
