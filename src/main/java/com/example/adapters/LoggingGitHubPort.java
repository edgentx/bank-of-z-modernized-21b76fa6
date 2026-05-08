package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fake adapter for GitHub used in development profiles to avoid
 * network calls and auth token requirements during local builds.
 */
public class LoggingGitHubPort implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingGitHubPort.class);

    @Override
    public String createIssue(String title, String body) {
        // Simulate a generated URL without calling GitHub API
        String fakeUrl = "https://github.com/bank-of-z/simulated-issues/" + System.currentTimeMillis();
        log.info("[GITHUB FAKE] Would create issue '{}' with URL: {}", title, fakeUrl);
        return fakeUrl;
    }
}
