package com.example.ports;

import java.util.Map;

/**
 * Port for GitHub issue creation and querying.
 */
public interface GitHubPort {
    /**
     * Creates a new issue in the GitHub repository.
     *
     * @param title       The title of the issue.
     * @param description The body of the issue.
     * @param labels      Map of labels or metadata to attach.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String description, Map<String, String> labels);
}