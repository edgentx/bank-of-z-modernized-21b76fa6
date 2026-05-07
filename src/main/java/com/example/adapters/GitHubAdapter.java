package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for creating issues on GitHub.
 * Implements the GitHubPort interface.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_BASE = "https://api.github.com";

    private final OkHttpClient httpClient;
    private final String githubApiToken;
    private final String repoOwner;
    private final String repoName;
    private final ObjectMapper objectMapper;

    public GitHubAdapter(
            @Value("${github.api.token}") String githubApiToken,
            @Value("${github.repo.owner}") String repoOwner,
            @Value("${github.repo.name}") String repoName,
            ObjectMapper objectMapper) {
        this.githubApiToken = githubApiToken;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String createIssue(String title, String body, Map<String, String> labels) {
        try {
            // Construct JSON payload
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("title", title);
            payloadMap.put("body", body);
            if (labels != null && !labels.isEmpty()) {
                // GitHub API expects labels as a list of strings
                payloadMap.put("labels", labels.values());
            }

            String jsonPayload = objectMapper.writeValueAsString(payloadMap);
            String url = String.format("%s/repos/%s/%s/issues", API_BASE, repoOwner, repoName);

            RequestBody requestBody = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + githubApiToken)
                    .addHeader("Accept", "application/vnd.github+json")
                    .addHeader("X-GitHub-Api-Version", "2022-11-28")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("GitHub API failed with code {}: {}", response.code(), response.body().string());
                    throw new RuntimeException("Failed to create GitHub issue. Code: " + response.code());
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String htmlUrl = jsonNode.path("html_url").asText();

                log.info("Successfully created GitHub issue: {}", htmlUrl);
                return htmlUrl;
            }
        } catch (IOException e) {
            log.error("IO Error creating GitHub issue", e);
            throw new RuntimeException("Error creating GitHub issue", e);
        }
    }
}
