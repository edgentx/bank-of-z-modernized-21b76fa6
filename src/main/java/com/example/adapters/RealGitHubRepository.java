package com.example.adapters;

import com.example.ports.GitHubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubRepository.
 * In a production environment, this would interact with the GitHub API to create issues.
 */
@Component
public class RealGitHubRepository implements GitHubRepository {

    private static final Logger logger = LoggerFactory.getLogger(RealGitHubRepository.class);

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Body cannot be empty");
        }

        // Placeholder for actual GitHub API logic
        // In a real scenario, we would:
        // 1. Construct the JSON payload for the issue.
        // 2. POST to https://api.github.com/repos/{owner}/{repo}/issues
        // 3. Parse the response to extract the HTML URL.
        logger.info("[GITHUB] Creating issue: {}", title);

        // Simulate successful creation and return a dummy URL for validation
        // In reality, this URL would come from the API response JSON field 'html_url'
        return "http://github.com/fake-repo/issues/1";
    }
}
