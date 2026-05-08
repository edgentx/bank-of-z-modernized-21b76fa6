package com.example.ports;

/**
 * Port interface for GitHub integration.
 * Acts as a boundary for the external GitHub API.
 */
public interface GitHubClient {

    /**
     * Creates or finds an issue for the given reference tag.
     * 
     * @param referenceTag The unique identifier (e.g. "VW-454")
     * @return The fully qualified URL to the GitHub issue
     * @throws RuntimeException if the issue cannot be created or found
     */
    String createIssueUrl(String referenceTag);
}
