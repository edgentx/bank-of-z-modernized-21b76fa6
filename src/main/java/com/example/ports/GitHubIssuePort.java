package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the browser URL for a specific GitHub issue ID.
     *
     * @param issueId The internal or external issue ID.
     * @return Optional containing the URL if found.
     */
    Optional<String> getIssueUrl(String issueId);
}
