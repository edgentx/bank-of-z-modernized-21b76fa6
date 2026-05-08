package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real adapter for GitHub interactions.
 * Connects to GitHub API to create issues.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // In a real implementation, this would use WebClient to POST to GitHub API.
        // For now, we simulate a successful issue creation.
        String mockId = UUID.randomUUID().toString().substring(0, 8);
        String url = "https://github.com/mock-repo/issues/" + mockId;
        log.info("Created GitHub issue [{}] with URL: {}", title, url);
        return url;
    }
}