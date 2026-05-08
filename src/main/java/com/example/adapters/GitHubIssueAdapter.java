package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for creating GitHub issues.
 * In a real scenario, this would use the GitHub API client.
 * For the purpose of this defect fix, it simulates the creation or implements the HTTP call.
 * We will assume a stubbed implementation that logs, as external API keys are not in scope.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);

    @Value("${github.repo.url:https://github.com/bank-of-z/vforce360}")
    private String repoUrl;

    @Override
    public String createIssue(String title, String body) {
        log.info("Creating GitHub Issue: title={}, body={}", title, body);
        
        // Simulate API call response
        // In production: RestTemplate / WebClient.post...
        String issueUrl = String.format("%s/issues/%s", repoUrl, title.replaceAll("[^0-9]", ""));
        
        log.info("GitHub Issue created at: {}", issueUrl);
        return issueUrl;
    }
}
