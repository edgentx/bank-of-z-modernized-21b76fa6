package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Real implementation of the GitHub Port.
 * In a production environment, this would query the GitHub API.
 * For this TDD Green phase, it acts as a structural placeholder satisfying the port contract.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final String BASE_URL = "https://github.com/egdcrypto/bank-of-z/issues/";

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return Optional.empty();
        }
        // Real implementation would check existence via API before returning
        String url = BASE_URL + issueId;
        log.debug("[MOCK-PROD] Resolved GitHub URL for {}: {}", issueId, url);
        return Optional.of(url);
    }
}
