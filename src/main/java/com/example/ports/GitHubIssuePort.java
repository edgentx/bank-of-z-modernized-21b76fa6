package com.example.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a GitHub issue based on the defect details.
     *
     * @param title The issue title.
     * @param body The issue body/description.
     * @return CompletableFuture containing the HTML URL of the created issue.
     */
    CompletableFuture<String> createIssue(String title, String body);
}
