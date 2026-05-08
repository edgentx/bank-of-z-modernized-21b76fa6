package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real implementation of GitHubIssuePort.
 * In a production environment, this would query the GitHub API.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        // Placeholder for production logic using GitHub API Client.
        // e.g., fetching GHClient.getIssueById(issueId).getHtmlUrl()
        log.warn("[GITHUB ADAPTER] Real API call not implemented for issue: {}", issueId);
        return Optional.empty();
    }
}
