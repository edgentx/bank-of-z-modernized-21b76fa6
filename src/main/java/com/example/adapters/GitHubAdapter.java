package com.example.adapters;

import com.example.ports.GitHubPort;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Default implementation of the GitHub Port.
 * Uses OkHttp to create issues via GitHub API.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final OkHttpClient client;
    private final String apiUrl;
    private final String authToken;

    public GitHubAdapter(
            @Value("${github.api.url}") String apiUrl,
            @Value("${github.auth.token}") String authToken) {
        this.apiUrl = apiUrl;
        this.authToken = authToken;
        this.client = new OkHttpClient();
    }

    @Override
    public Optional<String> createIssue(String title, String description) {
        // Construct JSON payload for GitHub Issue API
        // { "title": "...", "body": "..." }
        String jsonPayload = "{"
            + "\"title\": \"" + escape(title) + "\", "
            + "\"body\": \"" + escape(description) + "\""
            + "}";

        RequestBody body = RequestBody.create(
            jsonPayload,
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer " + authToken)
            .addHeader("Accept", "application/vnd.github+json")
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // Parse the HTML URL from the response body
                // Ideally use Jackson, but doing simple string extraction to avoid extra dependencies/configs
                String responseBody = response.body().string();
                String urlKey = "html_url";
                int urlIndex = responseBody.indexOf(urlKey);
                if (urlIndex != -1) {
                    int start = responseBody.indexOf("\"", urlIndex + urlKey.length()) + 1;
                    int end = responseBody.indexOf("\"", start);
                    if (end > start) {
                        return Optional.of(responseBody.substring(start, end));
                    }
                }
                // Fallback if parsing fails but request was 200 OK
                return Optional.of(apiUrl.replace("/api/v3/repos", "") + "/issues"); // Crude fallback
            }
        } catch (IOException e) {
            // Return empty on failure as per contract
            return Optional.empty();
        }
        
        return Optional.empty();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\"").replace("\n", "\\n").replace("\r", "");
    }
}
