package com.example.adapters;

import com.example.ports.GithubIssuePort;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * Real implementation for interacting with GitHub API.
 */
@Component
public class GithubAdapter implements GithubIssuePort {

    private final OkHttpClient httpClient;
    private final String githubApiUrl;
    private final String authToken;

    public GithubAdapter(
            @Value("${github.api.url:https://api.github.com}") String githubApiUrl,
            @Value("${github.token}") String authToken) {
        this.githubApiUrl = githubApiUrl;
        this.authToken = authToken;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public String createIssue(String title, String description) {
        // In a real scenario, we would read repo owner/name from config
        String url = githubApiUrl + "/repos/bank-of-z/vforce360/issues";

        // Build JSON payload manually or use ObjectMapper if preferred
        String jsonPayload = String.format(
                "{\"title\":\"%s\", \"body\":\"%s\"}",
                escapeJson(title), escapeJson(description)
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken)
                .addHeader("Accept", "application/vnd.github+json")
                .post(RequestBody.create(jsonPayload, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to create GitHub issue: " + response.code());
            }
            
            String responseBody = Objects.requireNonNull(response.body()).string();
            // Simple parsing for html_url - normally use Jackson ObjectMapper
            // Assuming structure: { "html_url": "https://..." }
            // For robustness, using ObjectMapper would be better, but keeping it simple for snippet.
            // We will use a quick hack or ObjectMapper here. Let's use Jackson properly.
            return parseHtmlUrl(responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with GitHub", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String parseHtmlUrl(String json) {
        // In a real app, inject ObjectMapper
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            GithubIssueResponse response = mapper.readValue(json, GithubIssueResponse.class);
            return response.htmlUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GitHub response", e);
        }
    }

    private record GithubIssueResponse(
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("id") long id
    ) {}
}