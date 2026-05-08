package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real implementation for GitHub issues.
 * This implementation is a stub that logs, but in a real environment would use a WebClient.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);

    @Override
    public Optional<String> createIssue(String title, String description) {
        // In a real scenario, this would use Spring's WebClient to call GitHub API.
        log.info("GITHUB Creating Issue: {}", title);
        // Simulating a failure or returning a mock URL for demonstration if not configured.
        // For the purpose of the 'real' adapter in this context, we return empty to simulate
        // a lack of configuration, or we could return a fake URL.
        return Optional.empty();
    }
}
