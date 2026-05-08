package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

/**
 * Real implementation of the GitHub Issue Port.
 * <p>
 * This adapter queries the GitHub API (or constructs the URL based on conventions)
 * to retrieve the specific URL for a reported defect ID.
 */
@Component
@ConditionalOnProperty(
    name = "integration.github.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private static final String GITHUB_ISSUE_URL_FORMAT = "https://github.com/%s/%s/issues/%s";

    private final String repoOwner;
    private final String repoName;
    private final WebClient webClient;

    public GitHubIssueAdapter(
            @Value("${integration.github.owner}") String owner,
            @Value("${integration.github.repo}") String repo,
            WebClient.Builder webClientBuilder) {
        this.repoOwner = owner;
        this.repoName = repo;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getIssueUrl(String defectId) {
        log.debug("Fetching URL for defect ID: {}", defectId);

        // Assumption: The defect ID maps 1:1 to a GitHub Issue number or is a valid identifier in the URL.
        // In a real scenario, we might query the API to verify existence, but for the URL generation
        // required by the defect report, string formatting is often sufficient.
        
        String url = String.format(GITHUB_ISSUE_URL_FORMAT, repoOwner, repoName, defectId);
        
        log.debug("Generated GitHub URL: {}", url);
        return url;
    }
}
