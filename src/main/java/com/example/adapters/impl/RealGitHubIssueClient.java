package com.example.adapters.impl;

import com.example.ports.GitHubIssuePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of GitHubIssuePort.
 * Would use GitHub REST API to create issues.
 */
@Component
@ConditionalOnProperty(name = "github.client.impl", havingValue = "real", matchIfMissing = false)
public class RealGitHubIssueClient implements GitHubIssuePort {

    private static final Logger logger = LoggerFactory.getLogger(RealGitHubIssueClient.class);

    @Override
    public String createIssue(String title, String body) {
        // In a real implementation, this would use an HTTP client to POST to GitHub API.
        logger.info("Creating GitHub issue with title: {}", title);
        
        // Placeholder for actual HTTP call and response parsing
        // String url = webClient.post()...
        // return url;
        
        return "https://github.com/bank-of-z/issues/REAL";
    }
}
