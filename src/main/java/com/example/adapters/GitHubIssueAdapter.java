package com.example.adapters;

import com.example.ports.GitHubIssuePort;

import java.net.URI;

/**
 * Real adapter for GitHub issues.
 * Would integrate with GitHub REST API in a production environment.
 */
public class GitHubIssueAdapter implements GitHubIssuePort {

    @Override
    public URI createIssue(String title, String body) throws Exception {
        // In a real implementation, this would:
        // 1. Authenticate with GitHub.
        // 2. POST to /repos/{owner}/{repo}/issues.
        // 3. Parse the response to extract the HTML URL.
        // For now, we simulate a successful creation returning a placeholder URI.
        return URI.create("https://github.com/example/bank-of-z/issues/1");
    }
}
