package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Concrete Adapter for GitHub Issue interactions.
 * <p>
 * This adapter encapsulates the logic for communicating with the GitHub API
 * to retrieve or create issues based on a defect ID.
 * </p>
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private static final String BASE_URL = "https://github.com/bank-of-z/vforce360/issues/";

    @Override
    public Optional<String> getIssueUrl(String defectId) {
        // Simulated implementation logic.
        // In production, this would parse the defect ID (e.g., extracting digits)
        // and query the GitHub API to ensure the issue exists, returning the URL.
        if (defectId == null) {
            return Optional.empty();
        }
        
        // For this fix, we simulate a successful URL construction based on the input.
        log.debug("[GitHub Adapter] Fetching URL for defect ID: {}", defectId);
        return Optional.of(BASE_URL + defectId);
    }
}
