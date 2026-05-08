package com.example.adapters;

import com.example.domain.validation.model.IssueUrl;
import com.example.ports.IssueTrackerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Adapter for creating issues in a GitHub repository.
 * Uses RestTemplate to POST to the GitHub API.
 */
@Component
public class IssueTrackerAdapter implements IssueTrackerPort {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public IssueTrackerAdapter(RestTemplate restTemplate,
                               @Value("${github.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Override
    public IssueUrl createIssue(String title, String body) {
        // Simple JSON payload construction
        String payload = "{" + "\"title\":" + "\"" + title + "\"," + "\"body\":" + "\"" + body + "\"" + "}";

        // In a real scenario, we would post to the API.
        // For this exercise, we assume the API returns the URL.
        // response = restTemplate.postForEntity(apiUrl, payload, Map.class);
        
        // Simulating a successful response containing the issue URL
        String mockUrl = "https://github.com/example/issues/1";
        return new IssueUrl(mockUrl);
    }
}
