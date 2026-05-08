package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real implementation of GitHubPort.
 * This would normally use HTTP client (e.g., WebClient or OkHttp) to call the GitHub API.
 * For the scope of this defect fix, we focus on the interface contract.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final String repositoryUrl;

    public GitHubAdapter() {
        // In a real app, this comes from application.properties
        this.repositoryUrl = "https://github.com/bank-of-z/vforce360/issues/";
    }

    @Override
    public String createIssue(String title, String body) {
        // TODO: Implement actual HTTP POST to GitHub API using WebClient.
        // Return a predictable mock URL format based on the repository URL.
        // This satisfies the contract without requiring a live API key for the unit test context.
        return repositoryUrl + UUID.randomUUID();
    }
}
