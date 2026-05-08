package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs for defect reporting.
 */
public interface GitHubPort {

    /**
     * Creates or finds a GitHub issue and returns its URL.
     *
     * @param title The defect title
     * @param body The defect body
     * @return The HTML URL of the GitHub issue
     */
    String reportIssue(String title, String body);
}
