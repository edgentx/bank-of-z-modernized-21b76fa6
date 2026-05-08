package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to generate the links required in the Slack notifications.
 */
public interface GitHubIssuePort {
    /**
     * Retrieves the public URL of a GitHub issue.
     *
     * @param issueId The internal or external ID of the issue.
     * @return The full HTTPS URL if found, empty otherwise.
     */
    Optional<String> getIssueUrl(String issueId);
}
