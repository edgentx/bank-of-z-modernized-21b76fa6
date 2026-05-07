package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for generating GitHub Issue URLs.
 * Configurable via application properties for organization and repository names.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private final String baseUrl;
    private final String org;
    private final String repo;

    /**
     * Constructs the adapter with configuration values.
     *
     * @param baseUrl The base GitHub URL (default: https://github.com).
     * @param org     The GitHub organization (e.g., "example-org").
     * @param repo    The repository name (e.g., "repo").
     */
    public GitHubIssueAdapter(
            @Value("${github.base-url:https://github.com}") String baseUrl,
            @Value("${github.org:example-org}") String org,
            @Value("${github.repo:repo}") String repo) {
        this.baseUrl = baseUrl;
        this.org = org;
        this.repo = repo;
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be blank");
        }
        return String.format("%s/%s/%s/issues/%s", baseUrl, org, repo, issueId);
    }
}
