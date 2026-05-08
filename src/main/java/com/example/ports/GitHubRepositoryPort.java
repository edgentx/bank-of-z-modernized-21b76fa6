package com.example.ports;

/**
 * Port interface for interacting with GitHub repositories.
 */
public interface GitHubRepositoryPort {

    /**
     * Creates a new GitHub issue for the given project and defect.
     *
     * @param projectId The internal project ID used to lookup the GitHub repo.
     * @param defectId The internal defect ID (e.g., VW-454).
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The full HTTPS URL to the created issue.
     */
    String createIssue(String projectId, String defectId, String title, String body);
}
