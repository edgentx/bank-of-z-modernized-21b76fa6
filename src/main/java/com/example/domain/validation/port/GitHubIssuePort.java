package com.example.domain.validation.port;

/**
 * Port for GitHub Issue operations.
 * Used to decouple the domain from the actual GitHub WebClient.
 */
public interface GitHubIssuePort {
    /**
     * Creates a GitHub issue and returns the HTML URL.
     */
    String createIssue(String title, String body, String labels);
}
