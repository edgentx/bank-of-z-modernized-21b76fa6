package com.example.adapters;

import com.example.ports.GitHubRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter implementation for GitHub Issue tracking using the REST API.
 */
@Component
public class GitHubRestAdapter implements GitHubRepositoryPort {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String apiToken;

    public GitHubRestAdapter(
            @Value("${github.api.url}") String apiUrl,
            @Value("${github.api.token}") String apiToken,
            OkHttpClient httpClient,
            ObjectMapper objectMapper) {
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String createIssue(String title, String body) {
        if (apiUrl == null || apiUrl.isBlank()) {
            throw new IllegalStateException("GitHub API URL is not configured.");
        }
        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException("GitHub API Token is not configured.");
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(
                    new IssueRequest(title, body)
            );

            RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonNode json = objectMapper.readTree(response.body().string());
                    return json.get("html_url").asText();
                } else {
                    throw new RuntimeException("Failed to create GitHub issue. Code: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with GitHub API", e);
        }
    }

    @Override
    public boolean isValidIssueUrl(String url) {
        return url != null && url.startsWith("https://github.com/") && url.contains("/issues/");
    }

    private record IssueRequest(String title, String body) {}
}
