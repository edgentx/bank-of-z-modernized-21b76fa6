package com.example.adapters;

import com.example.ports.GitHubPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Real implementation of GitHubPort using OkHttp.
 * Connects to GitHub API to create issues.
 */
public class OkHttpGitHubClient implements GitHubPort {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String repoApiUrl; // e.g., https://api.github.com/repos/org/repo/issues
    private final String authToken;  // For authentication

    // Constructor for configuration injection
    public OkHttpGitHubClient(String apiUrl, String authToken) {
        this.repoApiUrl = apiUrl;
        this.authToken = authToken;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String createIssue(String title, String body) {
        try {
            // Construct JSON payload manually or with ObjectMapper
            // Using simple string concatenation for efficiency to avoid extra DTO classes
            String jsonPayload = String.format(
                "{\"title\":\"%s\",\"body\":\"%s\"}",
                escapeJson(title),
                escapeJson(body)
            );

            RequestBody requestBody = RequestBody.create(
                jsonPayload,
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(repoApiUrl)
                    .addHeader("Authorization", "token " + authToken)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create GitHub issue: " + response.code() + " " + response.body().string());
                }
                
                String responseBody = response.body().string();
                // Parse the 'html_url' from the response
                // Simple parsing or use ObjectMapper. Using ObjectMapper for robustness.
                return objectMapper.readTree(responseBody).path("html_url").asText();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with GitHub API", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
