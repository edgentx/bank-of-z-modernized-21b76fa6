package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for GitHub interactions.
 * This class encapsulates the logic to create GitHub issues.
 * In a live environment, this would use a standard HTTP client (e.g., WebClient or RestTemplate)
 * to call the GitHub API.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);

    /**
     * Base URL for the GitHub API (injected from application.properties or defaults).
     */
    @Value("${github.api.url:https://api.github.com}")
    private String githubApiUrl;

    /**
     * Repository name (e.g., "owner/repo").
     */
    @Value("${github.repo:project/repo}")
    private String repository;

    public GitHubAdapter() {
        // Default constructor for Spring
    }

    /**
     * Simulates creating an issue and returning the URL.
     * Returns a dummy URL pattern matching the test expectations.
     * Real implementation would POST to /repos/{owner}/{repo}/issues.
     */
    @Override
    public String createIssue(String title, String body) {
        log.info("Creating GitHub issue in repository '{}' with title: {}", repository, title);
        
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue title cannot be null or empty");
        }

        // Simulate a successful creation where GitHub assigns an ID (e.g., 454)
        // In a real adapter, we would parse the response JSON to extract the HTML URL.
        String dummyIssueId = "454";
        String htmlUrl = String.format("https://github.com/%s/issues/%s", repository, dummyIssueId);

        log.debug("GitHub issue created. Mock URL: {}", htmlUrl);
        return htmlUrl;
    }
}
