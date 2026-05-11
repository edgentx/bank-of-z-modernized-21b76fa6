package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default implementation of GitHubPort.
 * In a real environment, this would use OkHttp or a GitHub client to create an issue.
 */
@Component
public class DefaultGitHubAdapter implements GitHubPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGitHubAdapter.class);

    @Override
    public String createIssue(String title, String description) {
        // Placeholder for real API call
        // POST /repos/{owner}/{repo}/issues
        logger.info("Creating GitHub issue: {}", title);
        
        // In a real implementation, we would parse the response to get the URL.
        // Returning a dummy URL to satisfy the contract if no real API is available.
        return "https://github.com/egdcrypto/bank-of-z/issues/" + System.currentTimeMillis();
    }
}