package com.example;

import org.springframework.stereotype.Component;

/**
 * Provider for GitHub Issue URLs.
 * In a real environment, this might query a database or a GitHub API.
 * For the scope of this validation, it generates the standard URL pattern.
 */
@Component
public class GitHubUrlProvider {

    private static final String BASE_URL = "https://github.com/bank-of-z/vforce360/issues";

    /**
     * Returns the GitHub URL for the given defect ID.
     *
     * @param defectId The ID (e.g., VW-454).
     * @return The full URL string.
     */
    public String getIssueUrl(String defectId) {
        return BASE_URL + "/" + defectId;
    }
}
