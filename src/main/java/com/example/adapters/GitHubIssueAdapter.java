package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubIssuePort.
 * In a real environment, this would use the GitHub Octokit or a standard REST client.
 */
@Component
@ConditionalOnProperty(name = "adapters.github.enabled", havingValue = "true", matchIfMissing = false)
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);

    @Override
    public String createIssue(String title, String description) {
        // Implementation of the actual GitHub API call would go here.
        // e.g., RestTemplate.postForEntity("https://api.github.com/repos/...")...
        log.info("Creating GitHub issue: {}", title);
        
        // Placeholder URL logic matching the Mock's deterministic pattern for demonstration
        return "https://github.com/example-bank/vforce360/issues/42";
    }
}
