package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issue tracking.
 */
public interface GitHubPort {
    
    /**
     * Creates a remote issue in GitHub based on the defect data.
     * @param summary The title of the issue.
     * @param description The body content of the issue.
     * @return The URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String summary, String description);
}
