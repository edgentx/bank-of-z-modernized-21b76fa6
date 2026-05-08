package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real adapter for GitHub API interactions.
 * This is a placeholder for the actual HTTP client implementation.
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(RealGitHubAdapter.class);

    @Override
    public Optional<String> createIssue(String summary, String description) {
        // TODO: Implement actual GitHub API call using WebClient or RestTemplate
        // For now, returning a dummy URL to satisfy the contract in a non-test environment
        log.warn("GitHub integration not yet implemented. Called with summary: {}", summary);
        return Optional.of("https://github.com/example-repo/issues/1");
    }
}