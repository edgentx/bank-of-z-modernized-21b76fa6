package com.example.adapters;

import com.example.ports.GitHubMetadataPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for retrieving GitHub metadata.
 * Connects to GitHub to resolve defect IDs to URLs.
 */
@Component
public class GitHubMetadataAdapter implements GitHubMetadataPort {

    private static final String BASE_ISSUE_URL = "https://github.com/example/bank-of-z/issues/";

    @Override
    public String getIssueUrl(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }
        // In a real scenario, this might query an API or perform a lookup.
        // Based on the defect description, we construct the URL.
        // Assuming defect ID format like VW-454 maps to issue number 454.
        String issueNumber = extractIssueNumber(defectId);
        return BASE_ISSUE_URL + issueNumber;
    }

    private String extractIssueNumber(String defectId) {
        // Simple extraction logic: take the number part after the hyphen.
        // "VW-454" -> "454"
        if (defectId.contains("-")) {
            return defectId.substring(defectId.lastIndexOf('-') + 1);
        }
        return defectId;
    }
}
