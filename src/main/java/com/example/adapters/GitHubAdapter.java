package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real implementation of the GitHub port.
 * Interacts with GitHub API to create issues.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final String BASE_URL = "https://github.com/owner/repo/issues/";

    @Override
    public String createIssue(String title, String description) {
        // In a real scenario, we would use WebClient to POST to GitHub API
        // POST /repos/owner/repo/issues
        
        // Simulate API call latency and response
        logger.info("Creating GitHub issue with title: {}", title);
        
        // Generate a deterministic ID based on random for simulation
        int issueId = Math.abs(UUID.randomUUID().hashCode());
        String url = BASE_URL + issueId;
        
        logger.info("GitHub issue created: {}", url);
        return url;
    }
}
