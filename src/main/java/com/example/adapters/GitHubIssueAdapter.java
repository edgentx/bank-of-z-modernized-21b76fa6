package com.example.adapters;

import com.example.ports.GitHubPort;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Real implementation of GitHubPort using OkHttp.
 * Configurable via Spring properties.
 */
@Component
public class GitHubIssueAdapter implements GitHubPort {

    private static final Logger logger = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String apiUrl;
    private final String token;

    public GitHubIssueAdapter(
            @Value("${github.api-url:https://api.github.com/repos/example/test/issues}") String apiUrl,
            @Value("${github.token:}") String token) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.token = token;
    }

    @Override
    public String createIssue(String title, String body) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", title);
            payload.put("body", body);

            String jsonPayload = mapper.writeValueAsString(payload);
            RequestBody requestBody = RequestBody.create(jsonPayload, JSON);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody);

            if (token != null && !token.isEmpty()) {
                requestBuilder.addHeader("Authorization", "token " + token);
            }
            requestBuilder.addHeader("Accept", "application/vnd.github.v3+json");

            Request request = requestBuilder.build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to create issue: {} {}", response.code(), response.body().string());
                    throw new RuntimeException("Failed to create GitHub issue: " + response.code());
                }

                String responseBody = response.body().string();
                Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
                String htmlUrl = (String) responseMap.get("html_url");
                
                if (htmlUrl == null) {
                    throw new RuntimeException("GitHub API response missing 'html_url'");
                }
                
                return htmlUrl;
            }
        } catch (IOException e) {
            logger.error("IO Error creating GitHub issue", e);
            throw new RuntimeException("Error communicating with GitHub", e);
        }
    }
}
