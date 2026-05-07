package com.example.ports;

import java.util.Optional;

/**
 * Port interface for GitHub interactions.
 * Used to mock the GitHub API in tests.
 */
public interface GitHubPort {
    /**
     * Creates an issue for the defect.
     * @param defectId The internal defect ID.
     * @return The URL of the created issue.
     */
    String createIssue(String defectId, String title, String description);
}
