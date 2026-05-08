package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the GitHubIssuePort.
 * In a production environment, this would fetch metadata from GitHub API.
 * Currently acts as a stub capable of generating standard URLs.
 */
@Component
public class RealGitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(RealGitHubIssueAdapter.class);

    @Value("${github.repo.url:https://github.com/bank-of-z/vforce360}")
    private String repoBaseUrl;

    @Override
    public String getIssueUrl(String issueId) {
        // In a real implementation, we might query the GitHub API to validate existence
        // or retrieve the actual numeric ID if the input is a key.
        // For this fix, we assume a direct mapping or standard URL structure.
        
        // Example: https://github.com/bank-of-z/vforce360/issues/454
        // We strip non-numeric chars from the ID if necessary, or append directly.
        // Assuming issueId might be "VW-454" or just "454".
        
        String numericId = issueId.replaceAll("[^0-9]", "");
        if (numericId.isEmpty()) {
            log.warn("Could not extract numeric ID from issueId: {}", issueId);
            return repoBaseUrl + "/issues/" + issueId; // Fallback
        }
        
        String url = repoBaseUrl + "/issues/" + numericId;
        log.debug("Resolved URL for {}: {}", issueId, url);
        return url;
    }
}
