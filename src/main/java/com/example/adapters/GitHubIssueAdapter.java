package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the GitHubIssuePort.
 * Constructs URLs based on a configured base GitHub URL.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private final String baseUrl;

    public GitHubIssueAdapter(@Value("${github.base-url:https://github.com/mock-org/repo/issues}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String generateIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be null");
        }
        // Ensure the base URL doesn't end with a slash to avoid double slashes,
        // or handle it gracefully. Based on the mock behavior, we expect a clean URL.
        String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return cleanBase + "/" + issueId;
    }
}
