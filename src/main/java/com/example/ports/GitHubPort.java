package com.example.ports;

import java.net.URI;

/**
 * Port interface for GitHub operations.
 * Used by the Report Defect workflow to generate links.
 */
public interface GitHubPort {
    /**
     * Generates a valid URI to a GitHub issue for the repository.
     * @param issueTitle The title of the issue (may be used to search, but here we generate the repo issues link).
     * @return A valid java.net.URI pointing to the GitHub issues page.
     */
    URI createIssueUrl(String issueTitle);
}
