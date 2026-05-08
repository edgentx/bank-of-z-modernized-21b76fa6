package com.example.domain.vforce.adapter;

import com.example.domain.vforce.model.GitHubIssue;
import com.example.domain.vforce.port.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for GitHub integration.
 * Currently a stub, would use WebClient or Octokit in production.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    @Override
    public GitHubIssue createIssue(String title, String body) {
        // Implementation would involve HTTP call to GitHub API
        // Returning a placeholder for now as this is driven by unit tests in isolation
        throw new UnsupportedOperationException("Real GitHub API call not implemented in this snippet");
    }
}
