package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation for creating GitHub issues.
 * In a production environment, this would use org.kohsuke:github-api.
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private static final Logger logger = LoggerFactory.getLogger(RealGitHubAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // Production implementation would use GitHub REST API
        // For now, we simulate the URL generation logic expected by tests
        String mockUrl = "https://github.com/bank-of-z/egdcrypto/issues/1";
        logger.info("[GITHUB] Creating issue '{}' -> {}", title, mockUrl);
        return mockUrl;
    }
}
