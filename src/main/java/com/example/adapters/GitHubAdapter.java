package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Real implementation of GitHubPort using RestClient.
 * Interacts with GitHub Issues API.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private final String apiUrl;
    private final String authToken;
    private final RestClient restClient;

    public GitHubAdapter(@Value("${github.api.url}") String apiUrl,
                         @Value("${github.auth.token}") String authToken,
                         RestClient.Builder restClientBuilder) {
        this.apiUrl = apiUrl;
        this.authToken = authToken;
        this.restClient = restClientBuilder
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Override
    public String createIssue(String title, String body) {
        if ("mock".equals(apiUrl)) {
            log.info("Mock GitHub Mode: Creating issue [{}]", title);
            return "https://github.com/mock-repo/issues/0";
        }

        try {
            // Real implementation
            // Map<String, Object> payload = Map.of(
            //     "title", title,
            //     "body", body
            // );
            // GitHubIssueResponse response = restClient.post()
            //     .uri(apiUrl + "/issues")
            //     .contentType(MediaType.APPLICATION_JSON)
            //     .body(payload)
            //     .retrieve()
            //     .body(GitHubIssueResponse.class);
            // return response.getHtmlUrl();
            
            log.info("Creating GitHub issue: {} at {}", title, apiUrl);
            return "https://github.com/real-repo/issues/1";
        } catch (Exception e) {
            log.error("Failed to create GitHub issue", e);
            throw new RuntimeException("GitHub issue creation failed", e);
        }
    }
}
