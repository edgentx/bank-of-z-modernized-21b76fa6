package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for interacting with GitHub issues.
 * Constructs standard GitHub URLs based on repository metadata.
 */
@Component
public class RealGitHubIssueAdapter implements GitHubIssuePort {

    private static final String GITHUB_BASE_URL = "https://github.com";

    public RealGitHubIssueAdapter() {
        // Default constructor for Spring Bean instantiation
    }

    @Override
    public String generateIssueUrl(String owner, String repo, int issueNumber) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("GitHub owner cannot be null or empty");
        }
        if (repo == null || repo.isBlank()) {
            throw new IllegalArgumentException("GitHub repo cannot be null or empty");
        }
        if (issueNumber <= 0) {
            throw new IllegalArgumentException("Issue number must be positive");
        }

        return String.format("%s/%s/%s/issues/%d", GITHUB_BASE_URL, owner, repo, issueNumber);
    }
}