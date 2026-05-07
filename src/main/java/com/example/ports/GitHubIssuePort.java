package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used to decouple the defect reporting logic from the actual GitHub implementation.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/454").
     */
    String createIssue(String title, String body);

}