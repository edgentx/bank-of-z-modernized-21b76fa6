package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter implementation for GitHubPort.
 * S-FB-1 Green Phase.
 */
@Service
public class GitHubPortImpl implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubPortImpl.class);

    @Override
    public String createIssue(String title, String description) {
        // Simulate GitHub Issue creation.
        // Returns a deterministic URL format expected by the domain logic.
        log.info("Creating GitHub issue: {}", title);
        
        // In a real implementation, this would use Spring's RestTemplate or WebClient.
        // For the E2E test flow via Temporal, this returns a mock URL.
        // We use System.currentTimeMillis() to ensure uniqueness similar to the Mock.
        return "https://github.com/mock-org/issues/" + System.currentTimeMillis();
    }
}