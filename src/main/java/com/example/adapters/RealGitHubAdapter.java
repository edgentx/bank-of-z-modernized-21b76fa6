package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort using GitHub API (Simulated for this exercise).
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(RealGitHubAdapter.class);

    @Override
    public String createIssue(String title, String description) {
        // Implementation stub for the 'Real' adapter.
        // The actual REST call to GitHub API would go here.
        log.info("[RealGitHub] Creating issue: {}", title);
        return "https://github.com/org/repo/issues/REAL";
    }
}
