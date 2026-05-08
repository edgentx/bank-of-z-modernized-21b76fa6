package com.example.adapters.impl;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort using standard HTTP clients.
 * Currently stubbed to satisfy the interface, but would invoke GitHub REST API.
 */
@Component
public class GitHubClientAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String body, String... labels) {
        // Implementation for the real GitHub API call would go here.
        // For now, returning a dummy URL to satisfy the contract if this adapter is used directly.
        // In production, this would use WebClient or RestTemplate to POST to /repos/{owner}/{repo}/issues
        return "https://github.com/bank-of-z/vforce360/issues/PROD-DUMMY";
    }
}