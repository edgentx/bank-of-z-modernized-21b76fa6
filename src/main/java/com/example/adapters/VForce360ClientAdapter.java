package com.example.adapters;

import com.example.ports.VForce360Client;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real Adapter implementation for VForce360Client.
 * Generates the body of a Slack message containing a GitHub link.
 */
@Component
public class VForce360ClientAdapter implements VForce360Client {

    private static final Logger log = LoggerFactory.getLogger(VForce360ClientAdapter.class);
    private static final String GITHUB_BASE_URL = "https://github.com/org/repo/issues/";

    @Override
    public String reportDefect(String defectTitle, String projectId, String severity) {
        log.info("Reporting defect '{}' for project '{}' with severity '{}'", defectTitle, projectId, severity);

        // 1. Construct the GitHub URL based on the defect title (e.g., VW-454 -> 454)
        String issueNumber = extractIssueNumber(defectTitle);
        String githubUrl = GITHUB_BASE_URL + issueNumber;

        // 2. Format the Slack body with the link
        // Slack link format: <url|text> or just <url>
        String body = "Defect Reported: " + defectTitle + " - See <" + githubUrl + ">";

        log.debug("Generated body: {}", body);
        return body;
    }

    /**
     * Extracts the numeric issue ID from a title like "VW-454".
     */
    private String extractIssueNumber(String defectTitle) {
        if (defectTitle == null || defectTitle.isEmpty()) {
            return "";
        }
        // Split by '-' and take the last part
        String[] parts = defectTitle.split("-");
        return parts[parts.length - 1];
    }
}
