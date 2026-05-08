package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for GitHub interactions.
 * This is a placeholder implementation that would normally wrap an HTTP client.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);

    @Override
    public String createIssue(String title, String description, String component) {
        // In a real scenario, this would use WebClient or RestTemplate to call GitHub API.
        // For the defect S-FB-1, we return a valid URL structure.
        log.info("Creating GitHub issue for component: {}, title: {}", component, title);
        
        // Simulating a unique ID generation for the issue
        String mockIssueId = "" + System.currentTimeMillis();
        return "https://github.com/bank-of-z/issues/" + mockIssueId;
    }
}
