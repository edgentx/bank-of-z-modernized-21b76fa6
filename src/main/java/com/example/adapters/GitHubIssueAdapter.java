package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class GitHubIssueAdapter implements GitHubPort {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String repoOwner;
    private final String repoName;
    private final String githubToken;
    private final String apiUrl;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Constructor injection required for Spring Config
    public GitHubIssueAdapter(OkHttpClient client,
                              ObjectMapper objectMapper,
                              @Value("${github.repo.owner}") String repoOwner,
                              @Value("${github.repo.name}") String repoName,
                              @Value("${github.token}") String githubToken) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.githubToken = githubToken;
        this.apiUrl = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/issues";
    }

    @Override
    public String createIssue(String title, String body, Map<String, String> labels) {
        try {
            Map<String, Object> payload = Map.of(
                "title", title,
                "body", body,
                "labels", labels.keySet().toArray() // Simple extraction of keys as labels
            );

            String jsonBody = objectMapper.writeValueAsString(payload);

            Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "token " + githubToken)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create GitHub issue: " + response.code());
                }
                // Extract URL from response
                String responseBody = response.body().string();
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                return (String) responseMap.get("html_url");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with GitHub API", e);
        }
    }
}
