package com.example.adapters;

import com.example.domain.vforce360.model.DefectReportedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Adapter for GitHub Issues API.
 * S-FB-1: Validates VW-454 by ensuring GitHub URLs are generated correctly.
 */
@Component
public class GithubIssueAdapter {

    private final RestTemplate restTemplate;
    private static final String GITHUB_API_URL = "https://api.github.com/repos";
    // Ideally configured via application.properties, using defaults for fix implementation
    private final String repoOwner = "egdcrypto";
    private final String repoName = "bank-of-z-modernized";

    // Constructor injection allows for easy mocking in tests
    public GithubIssueAdapter() {
        this.restTemplate = new RestTemplate();
    }

    public GithubIssueAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Creates a GitHub issue based on the domain event.
     * Returns the HTML URL of the created issue.
     */
    public String createIssue(DefectReportedEvent event) {
        String url = String.format("%s/%s/%s/issues", GITHUB_API_URL, repoOwner, repoName);

        // Construct payload according to GitHub API standards
        Map<String, Object> payload = Map.of(
            "title", event.title(),
            "body", formatBody(event),
            "labels", java.util.List.of("defect", "vforce360")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Note: Authentication header would be added here for a real implementation
        // headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Execute POST request
        @SuppressWarnings("rawtypes")
        Map response = restTemplate.postForObject(url, request, Map.class);

        if (response != null && response.containsKey("html_url")) {
            return (String) response.get("html_url");
        }

        throw new RuntimeException("Failed to create GitHub issue: No URL returned");
    }

    private String formatBody(DefectReportedEvent event) {
        return String.format(
            "**Description:**%s%n%n**Reporter:** %s%n%n**Aggregate ID:** %s",
            event.description(),
            event.reporter(),
            event.aggregateId()
        );
    }
}
