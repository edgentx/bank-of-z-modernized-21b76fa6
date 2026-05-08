package com.example.domain.validation.port;

import com.example.domain.validation.model.GitHubIssueUrl;

/**
 * Port for creating or finding GitHub Issues.
 * Abstracts the GitHub API interaction.
 */
public interface GitHubIssuePort {
    /**
     * Creates an issue on GitHub and returns the direct URL to it.
     */
    GitHubIssueUrl createIssue(String title, String description);
}
