package com.example.domain.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Port for creating GitHub issues.
 * Used by domain services to avoid direct dependency on GitHub API implementation.
 */
public interface GitHubIssuePort {
    /**
     * Creates a GitHub issue based on the defect report.
     *
     * @param title       The title of the issue.
     * @param description The description/body of the issue.
     * @return CompletableFuture containing the URL of the created issue.
     */
    CompletableFuture<String> createIssue(String title, String description);
}