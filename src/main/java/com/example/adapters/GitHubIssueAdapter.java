package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Real implementation of GitHubIssuePort using OkHttp.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String authToken;

    public GitHubIssueAdapter(
            OkHttpClient client,
            ObjectMapper objectMapper,
            @Value("${vforce360.github.api.url}") String apiUrl,
            @Value("${vforce360.github.auth.token}") String authToken) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.apiUrl = apiUrl;
        this.authToken = authToken;
    }

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        try {
            GitHubIssueRequest issueRequest = new GitHubIssueRequest(title, body);
            String jsonBody = objectMapper.writeValueAsString(issueRequest);

            RequestBody bodyReq = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(bodyReq)
                    .build();

            CompletableFuture<String> future = new CompletableFuture<>();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("GitHub issue creation failed", e);
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (response) {
                        if (!response.isSuccessful()) {
                            future.completeExceptionally(new IOException("Unexpected code " + response));
                        } else {
                            String responseBody = response.body().string();
                            GitHubIssueResponse issueResponse = objectMapper.readValue(responseBody, GitHubIssueResponse.class);
                            future.complete(issueResponse.htmlUrl());
                        }
                    }
                }
            });

            return future;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private record GitHubIssueRequest(String title, String body) {}
    private record GitHubIssueResponse(String htmlUrl, int id) {}
}
