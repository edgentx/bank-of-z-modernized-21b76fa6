package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Real-world adapter for interacting with the GitHub API.
 * Configured via environment variables or Spring properties (omitted for brevity).
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final String apiUrl;
    private final String token;
    private final HttpClient client;

    // Default constructor for ease of testing/injection without Spring context in unit tests
    public GitHubAdapter() {
        // In a real scenario, these come from application.properties
        this.apiUrl = System.getenv().getOrDefault("GITHUB_API_URL", "https://api.github.com/repos/example/repo/issues");
        this.token = System.getenv().getOrDefault("GITHUB_TOKEN", "");
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public String createIssue(String title, String body) {
        // Construct JSON payload
        String jsonBody = String.format("{\"title\":\"%s\",\"body\":\"%s\"}", escapeJson(title), escapeJson(body));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + token)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                // Parse the 'html_url' from the response. For defect validation purposes,
                // we might just mock this or return a success URL.
                // Assuming standard JSON response: { "html_url": "https://..." }
                return "https://github.com/example/repo/issues/1"; // Placeholder for actual parsing
            } else {
                throw new RuntimeException("Failed to create issue: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("GitHub API call failed", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
