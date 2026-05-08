package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation for GitHub API interactions.
 * Currently a placeholder to satisfy the Port contract.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String body) {
        // In a real implementation, this would use WebClient or RestTemplate
        // to POST to https://api.github.com/repos/owner/repo/issues
        // For now, we return a dummy URL to satisfy compiler requirements.
        return "https://github.com/bank-of-z/vforce360/issues/0";
    }
}
