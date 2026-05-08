package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * Generates URLs based on the configured repository.
 */
@Component
public class GitHubRestAdapter implements GitHubPort {

    private final String repoUrl;

    public GitHubRestAdapter(@Value("${github.repo.url:https://github.com/mock-org/bank-of-z}") String repoUrl) {
        // Normalize URL (remove trailing slash)
        this.repoUrl = repoUrl.endsWith("/") ? repoUrl.substring(0, repoUrl.length() - 1) : repoUrl;
    }

    @Override
    public String generateIssueUrl(String issueId) {
        // GitHub URL pattern: <repo_url>/issues/<issue_id>
        // Example: https://github.com/org/repo/issues/VW-454
        return this.repoUrl + "/issues/" + issueId;
    }

    @Override
    public String getRepositoryUrl() {
        return repoUrl;
    }
}
