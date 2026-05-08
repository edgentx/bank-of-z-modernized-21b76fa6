package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub Client Implementation using OkHttp.
 */
@Component
public class OkHttpGitHubClient implements GitHubPort {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String apiUrl;
    private final String authToken;

    public OkHttpGitHubClient(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        // Configuration typically from application.properties
        this.apiUrl = System.getenv().getOrDefault("GITHUB_API_URL", "https://api.github.com");
        this.authToken = System.getenv().getOrDefault("GITHUB_TOKEN", "");
    }

    @Override
    public String createIssue(String repo, String title, String body) {
        try {
            String url = apiUrl + "/repos/" + repo + "/issues";

            Map<String, Object> payload = new HashMap<>();
            payload.put("title", title);
            payload.put("body", body);

            String jsonPayload = mapper.writeValueAsString(payload);

            RequestBody requestBody = RequestBody.create(
                jsonPayload,
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "token " + authToken)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(requestBody)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create GitHub issue: " + response.code());
                }

                String responseBody = response.body().string();
                // Parse the 'html_url' from the response
                Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                return (String) responseMap.get("html_url");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with GitHub", e);
        }
    }
}
