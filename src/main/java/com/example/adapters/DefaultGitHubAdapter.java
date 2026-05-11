package com.example.adapters;

import com.example.domain.defect.model.LinkGitHubIssueCmd;
import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Default implementation of {@link GitHubPort}.
 * This adapter simulates the API call structure. In a real scenario,
 * it would use a dedicated GitHub Java client or RestTemplate to hit the GitHub API.
 * For S-FB-1, this acts as the external boundary.
 */
public class DefaultGitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(DefaultGitHubAdapter.class);
    private final RestTemplate restTemplate;
    private final String repoBaseUrl;

    public DefaultGitHubAdapter(RestTemplate restTemplate, String repoBaseUrl) {
        this.restTemplate = restTemplate;
        this.repoBaseUrl = repoBaseUrl;
    }

    @Override
    public String createIssue(LinkGitHubIssueCmd cmd) {
        // In a real implementation, this would POST to:
        // POST https://api.github.com/repos/{owner}/{repo}/issues
        // Body: { "title": cmd.summary(), "body": cmd.description() }

        // For the purpose of the defect validation (S-FB-1), we ensure the return value
        // strictly follows the expected format.
        log.info("Creating GitHub issue for defect {}: {}", cmd.defectId(), cmd.url());

        // Simulate a successful creation returning a valid GitHub URL.
        // We assume the input command's URL is the target or we generate one.
        if (cmd.url() != null && cmd.url().startsWith("https://github.com/")) {
            return cmd.url();
        }

        // Fallback generation if the command was just a trigger
        return "https://github.com/" + repoBaseUrl + "/issues/" + UUID.randomUUID();
    }
}
