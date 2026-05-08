package com.example.infrastructure.adapters;

import com.example.domain.validation.port.GitHubPort;
import com.example.infrastructure.adapters.github.GitHubIssueClient;
import com.example.infrastructure.adapters.github.GitHubIssueRequest;
import com.example.infrastructure.adapters.github.GitHubIssueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Real-world implementation of GitHubPort using Spring Cloud OpenFeign.
 * Connects to GitHub API to create issues.
 */
@Component
public class GitHubFeignAdapter implements GitHubPort {

    private final GitHubIssueClient client;
    private final String repoOwner;
    private final String repoName;

    @Autowired
    public GitHubFeignAdapter(GitHubIssueClient client,
                              @Value("${github.owner}") String repoOwner,
                              @Value("${github.repo}") String repoName) {
        this.client = client;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    @Override
    public String createIssue(String title, String body, String... labels) {
        GitHubIssueRequest request = new GitHubIssueRequest(title, body, Arrays.asList(labels));
        
        GitHubIssueResponse response = client.createIssue(repoOwner, repoName, request);
        
        if (response != null && response.htmlUrl() != null) {
            return response.htmlUrl();
        }
        throw new IllegalStateException("Failed to retrieve URL from GitHub response");
    }
}
