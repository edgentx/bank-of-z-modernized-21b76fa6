package com.example.adapters;

import com.example.ports.GithubPort;
import com.squareup.okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Real adapter for creating issues on GitHub.
 * Uses OkHttp for HTTP communication.
 */
@Component
public class GithubAdapter implements GithubPort {

    private static final Logger log = LoggerFactory.getLogger(GithubAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiUrl;
 private final String token; // In a real scenario, use a secure vault or env variable

    public GithubAdapter(@Value("${github.api.url}") String apiUrl,
                         @Value("${github.api.token}") String token) {
        this.client = new OkHttpClient();
        this.apiUrl = apiUrl;
        this.token = token;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simplified JSON payload construction
        String json = "{\"title\":\"" + escapeJson(title) + "\", \"body\":\"" + escapeJson(body) + "\"}";

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "token " + token)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(RequestBody.create(json, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to create GitHub issue: {}", response.body());
                throw new RuntimeException("Failed to create issue: " + response.code());
            }
            
            // Parse response to get URL. In a real app, use Jackson or Gson.
            // For TDD simplicity, assuming the API returns a standard JSON structure.
            String respBody = response.body().string();
            // Naive parsing for the sake of the exercise, as external json libs might not be configured yet
            if (respBody.contains("html_url")) {
                int start = respBody.indexOf("html_url") + 10;
                char separator = respBody.charAt(start);
                int end = respBody.indexOf(separator, start + 1);
                return respBody.substring(start + 1, end);
            }
            throw new RuntimeException("Unknown response format from GitHub");
        } catch (IOException e) {
            log.error("IO Error creating GitHub issue", e);
            throw new RuntimeException("Error communicating with GitHub", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}