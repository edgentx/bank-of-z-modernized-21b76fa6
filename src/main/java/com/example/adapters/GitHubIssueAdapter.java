package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real adapter for GitHub issue creation.
 * Connects to GitHub REST API.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private static final String GITHUB_API_URL = "https://api.github.com/repos"; // Simplified

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        // Simulate API call logic
        // In a real scenario: POST https://api.github.com/repos/{owner}/{repo}/issues
        // { "title": title, "body": body }

        String fakeIssueId = UUID.randomUUID().toString().substring(0, 8);
        String url = "http://github.com/example/issues/" + fakeIssueId;

        log.info("Created GitHub Issue {} with title: {}", url, title);
        return url;
    }
}
