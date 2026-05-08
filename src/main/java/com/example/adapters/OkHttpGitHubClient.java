package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter for GitHub Issues API using OkHttp.
 */
public class OkHttpGitHubClient implements GitHubPort {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String apiUrl;
    private final String authToken;

    public OkHttpGitHubClient(String repoOwner, String repoName, String authToken) {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.apiUrl = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/issues";
        this.authToken = authToken;
    }

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, String> payload = Map.of(
                    "title", title,
                    "body", body
                );

                RequestBody jsonBody = RequestBody.create(
                    mapper.writeValueAsBytes(payload),
                    MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(jsonBody)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("GitHub API error: " + response.code());
                    }
                    String responseBody = response.body().string();
                    return mapper.readTree(responseBody).path("html_url").asText();
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create GitHub issue", e);
            }
        });
    }
}