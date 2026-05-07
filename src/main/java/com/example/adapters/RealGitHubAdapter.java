package com.example.adapters;

import com.example.ports.GitHubPort;
import okhttp3.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

/**
 * Real implementation of GitHubPort using OkHttp.
 * Requires a GITHUB_TOKEN and GITHUB_REPO environment variable (e.g., "owner/repo").
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private final OkHttpClient client = new OkHttpClient();
    private final String token;
    private final String repo;
    private static final String API_BASE = "https://api.github.com";

    public RealGitHubAdapter() {
        // In a real Spring Boot app, use @Value
        this.token = System.getenv("GITHUB_TOKEN");
        this.repo = System.getenv("GITHUB_REPO");
        if (this.token == null || this.repo == null) {
             throw new IllegalStateException("GITHUB_TOKEN and GITHUB_REPO environment variables must be set");
        }
    }

    @Override
    public String createIssue(String title, String body) {
        // Construct JSON payload
        String jsonPayload = "{\"title\": \"" + escape(title) + "\", \"body\": \"" + escape(body) + "\"}";

        RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_BASE + "/repos/" + repo + "/issues")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/vnd.github+json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to create GitHub issue: " + response.code() + " " + response.body().string());
            }
            String responseBody = response.body().string();
            // Simple parsing of the URL from the response (avoiding full JSON lib dependency for this stub)
            // Ideally, use Jackson or Gson here.
            String htmlUrl = responseBody.substring(responseBody.indexOf("html_url") + 10);
            htmlUrl = htmlUrl.substring(0, htmlUrl.indexOf("\""));
            return htmlUrl;
        } catch (IOException e) {
            throw new RuntimeException("IOException creating GitHub issue", e);
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}