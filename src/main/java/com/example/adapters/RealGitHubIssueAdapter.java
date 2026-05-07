package com.example.adapters;

import com.example.ports.GitHubIssuePort;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Real implementation of GitHubIssuePort.
 * Would perform an HTTP POST to the GitHub API to create an issue.
 */
public class RealGitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger logger = Logger.getLogger(RealGitHubIssueAdapter.class.getName());

    @Override
    public URI createIssue(String title, String body) {
        // In a real implementation, we would use RestTemplate or WebClient to call GitHub API.
        // For now, we return a mock URI to satisfy the contract.
        // This would be replaced by logic parsing the response from GitHub API.
        
        String fakeUrl = "https://github.com/bank-of-z/issues/" + System.currentTimeMillis();
        logger.info("Creating GitHub issue: " + title + " at " + fakeUrl);
        
        return URI.create(fakeUrl);
    }
}
