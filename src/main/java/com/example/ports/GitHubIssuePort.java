package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Records a defect in GitHub and returns the URL.
     *
     * @param title The title of the issue
     * @param body  The body of the issue
     * @return The full URL to the created GitHub issue
     */
    String createIssue(String title, String body);
}
