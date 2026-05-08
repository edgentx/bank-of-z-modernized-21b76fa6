package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of GitHubIssuePort.
 * In a real environment, this would use the Octokits or HttpClients.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger logger = LoggerFactory.getLogger(GitHubIssueAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // Real implementation would use GitHub REST API here.
        logger.info("[GITHUB MOCK] Creating issue '{}' with body: {}", title, body);
        
        // Returning a deterministic dummy URL for the test environment to function.
        // In a real adapter, we would parse the response URL.
        return "https://github.com/fake-org/vforce360/issue/1";
    }
}
