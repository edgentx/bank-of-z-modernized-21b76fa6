package com.example.ports;

import java.util.Map;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title  The title of the issue.
     * @param body   The body content of the issue.
     * @param labels Optional labels to add to the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body, Map<String, String> labels);
}
