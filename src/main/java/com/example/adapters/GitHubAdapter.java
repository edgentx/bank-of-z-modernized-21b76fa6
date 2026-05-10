package com.example.adapters;

import com.example.domain.ports.GitHubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter for creating GitHub issues via HTTP API.
 * This implementation requires a valid GitHub Personal Access Token
 * configured via application properties.
 */
@Component
public class GitHubAdapter implements GitHubRepository {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String apiUrl;
    private final String token;
    private final ObjectMapper objectMapper;

    public GitHubAdapter(
            @Value("${github.api.url:https://api.github.com/repos/fake-org/repo}") String apiUrl,
            @Value("${github.api.token:}") String token,
            OkHttpClient httpClient, // Injected to allow customization in tests/config if needed
            ObjectMapper objectMapper
    ) {
        this.apiUrl = apiUrl;
        this.token = token;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String createIssue(String title, String description) {
        log.info("Creating GitHub issue: {}", title);

        try {
            // Create JSON Payload manually or using ObjectMapper
            String jsonPayload = String.format(
                    "{\"title\":\"%s\", \"body\":\"%s\"}",
                    escapeJson(title),
                    escapeJson(description)
            );

            RequestBody body = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder()
                    .url(apiUrl + "/issues")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to create issue: {} {}", response.code(), response.message());
                    // Fallback or throw exception based on business logic requirements
                    // For defect reporting flow, we might want to fail fast or return a default
                    throw new RuntimeException("GitHub API call failed with code: " + response.code());
                }

                String responseBody = response.body().string();
                // Parse URL from response: { "html_url": "https://github.com/...", ... }
                String url = objectMapper.readTree(responseBody).path("html_url").asText();
                
                log.info("Issue created successfully: {}", url);
                return url;
            }
        } catch (IOException e) {
            log.error("IO Error communicating with GitHub", e);
            throw new RuntimeException("Failed to create GitHub issue", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
