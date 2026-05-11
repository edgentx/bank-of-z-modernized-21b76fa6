package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs for reported defects.
 */
public interface GitHubIssuePort {
    
    /**
     * Generates the full URL for a specific GitHub issue.
     * 
     * @param issueId The ID of the issue (e.g. "VW-454")
     * @return The https URL to the issue
     */
    String getIssueUrl(String issueId);
}
