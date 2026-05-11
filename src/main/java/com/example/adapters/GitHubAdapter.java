package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Real adapter for GitHub integration.
 * Construct valid GitHub URLs based on configuration.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);

    private final String githubBaseUrl;
    private final String githubOwner;
    private final String githubRepo;

    public GitHubAdapter(
            @Value("${vforce.github.base-url:https://github.com}") String githubBaseUrl,
            @Value("${vforce.github.owner:example-org}") String githubOwner,
            @Value("${vforce.github.repo:bank-of-z-repo}") String githubRepo) {
        this.githubBaseUrl = githubBaseUrl;
        this.githubOwner = githubOwner;
        this.githubRepo = githubRepo;
    }

    @Override
    public URI createIssueUrl(String issueTitle) {
        try {
            // Construct standard GitHub issue URL: https://github.com/{owner}/{repo}/issues
            String path = String.format("%s/%s/%s/issues", githubOwner, githubRepo);
            return new URI(githubBaseUrl).resolve(path);
        } catch (URISyntaxException e) {
            log.error("Invalid GitHub configuration", e);
            throw new IllegalStateException("Invalid GitHub configuration", e);
        } catch (IllegalArgumentException e) {
            log.error("GitHub URL construction failed", e);
            throw new IllegalStateException("GitHub URL construction failed", e);
        }
    }
}
