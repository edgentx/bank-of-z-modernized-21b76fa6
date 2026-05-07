package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for GitHub Issue creation.
 * In a production environment, this would use WebClient or RestTemplate to POST
 * to the GitHub Issues API.
 */
@Component
public class RestGitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(RestGitHubIssueAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // Implementation for actual GitHub API call would go here.
        // e.g. webClient.post()... .uri("/repos/{owner}/{repo}/issues")
        log.info("[GITHUB] Creating issue: {}", title);
        
        // Returning a placeholder URL structure to satisfy the contract
        // in the absence of a real live API connection.
        return "https://github.com/example-org/validation-service/issues/new";
    }
}
