package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * This adapter would use a GitHub client (e.g., Octokit or standard HTTP client) to create issues.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String body) {
        // Real execution: Call GitHub REST API
        // POST /repos/{owner}/{repo}/issues
        // return response.htmlUrl();
        
        // Placeholder to simulate successful creation and URL return
        return "https://github.com/fake-org/project/issues/" + System.currentTimeMillis();
    }
}
