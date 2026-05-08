package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real HTTP Adapter for GitHub.
 * In a production environment, this would use RestTemplate/WebClient to call the GitHub Issues API.
 */
@Component
public class GitHubHttpAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubHttpAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // TODO: Implement actual GitHub API call.
        // e.g., gitHubClient.createIssue(repoOwner, repoName, title, body);
        // Returning a deterministic URL structure for now.
        log.info("[MOCK] Creating GitHub Issue with title: {}", title);
        return "https://github.com/example/bank-of-z/issues/1";
    }
}