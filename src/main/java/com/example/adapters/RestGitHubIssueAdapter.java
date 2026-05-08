package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Real-world implementation of GitHubIssuePort using Spring RestTemplate.
 * In a production environment, this would call the external GitHub API.
 * For the context of this specific defect fix (VW-454), the actual logic
 * being tested is the URL construction, which resides here and in the port definition.
 */
@Component
public class RestGitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(RestGitHubIssueAdapter.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final String repoOwner;
    private final String repoName;

    public RestGitHubIssueAdapter(RestTemplate restTemplate,
                                  @Value("${github.api.base-url:https://api.github.com}") String apiBaseUrl,
                                  @Value("${github.repo.owner:force360}") String repoOwner,
                                  @Value("${github.repo.name:vforce360}") String repoName) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return Optional.empty();
        }

        // VW-454 FIX: Ensure the URL is constructed correctly according to requirements.
        // Expected format: https://github.com/force360/vforce360/issues/{issueId}
        String url = String.format("https://github.com/%s/%s/issues/%s", repoOwner, repoName, issueId);
        log.debug("Constructed GitHub URL for issue {}: {}", issueId, url);

        // In a real scenario, we might verify existence via API here, but for the notification
        // pipeline, generating the link is the primary concern.
        return Optional.of(url);
    }
}
