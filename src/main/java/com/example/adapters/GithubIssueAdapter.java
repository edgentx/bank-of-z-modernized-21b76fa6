package com.example.adapters;

import com.example.ports.GithubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real-world adapter for GitHub issues.
 * Currently acts as a stub that returns a deterministic URL format.
 * In a full implementation, this would use Octokit or a standard HTTP client.
 */
@Component
public class GithubIssueAdapter implements GithubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GithubIssueAdapter.class);
    private static final String BASE_URL = "https://github.com/bank-of-z/vforce360/issues/";

    @Override
    public String createIssue(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue title cannot be empty");
        }

        // Stub implementation: Return a mock URL containing a random ID
        // Real implementation would POST to GitHub API and return the location header
        String mockIssueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String url = BASE_URL + mockIssueId;

        log.info("[GITHUB] Creating issue '{}'. URL: {}", title, url);
        return url;
    }
}
