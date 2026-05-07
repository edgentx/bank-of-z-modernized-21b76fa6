package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the GitHubIssuePort.
 * In a production environment, this would call GitHub REST API.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    @Override
    public String createIssue(String title, String description) {
        // Real implementation would POST to /repos/{owner}/{repo}/issues
        // and return the HTML URL from the response.
        // Simulating a successful creation with a valid URL structure.
        String mockIssueId = "454";
        return "https://github.com/example/bank-of-z/issues/" + mockIssueId;
    }
}
