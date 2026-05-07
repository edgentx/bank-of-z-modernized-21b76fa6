package com.example.ports;

import java.net.URI;

/**
 * Port interface for GitHub Issue creation.
 */
public interface GitHubIssuePort {

    /**
     * Creates a GitHub issue.
     *
     * @param title The title of the issue
     * @param body  The body of the issue
     * @return The URL of the created issue
     */
    URI createIssue(String title, String body);
}
