package com.vforce360.validation.ports;

/**
 * Port interface for GitHub REST API interactions.
 * Used to create issues and retrieve metadata.
 */
public interface GitHubRestClientPort {
    
    /**
     * Creates a new issue in the repository.
     * @param request The issue details.
     * @return The URL of the created issue.
     */
    String createIssue(CreateIssueRequest request);
}
