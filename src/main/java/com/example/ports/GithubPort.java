package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GithubPort {

    /**
     * Creates a GitHub issue for the given defect details.
     *
     * @param title The issue title
     * @param body The issue body
     * @return The URL of the created issue
     */
    String createIssue(String title, String body);
}
