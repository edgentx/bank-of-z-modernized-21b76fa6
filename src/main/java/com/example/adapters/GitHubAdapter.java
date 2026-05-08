package com.example.adapters;

import com.example.ports.GitHubPort;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Real adapter for GitHub Issue creation.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAdapter.class);
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();

    @Value("${github.api.url:https://api.github.com/repos/example-org/bank-of-z-modernization/issues}")
    private String apiUrl;

    @Value("${github.auth.token:}")
    private String authToken;

    @Override
    public String createIssue(String title, String body, String... labels) {
        logger.info("Creating GitHub Issue: {}", title);

        Map<String, Object> issueData = new HashMap<>();
        issueData.put("title", title);
        issueData.put("body", body);
        if (labels != null && labels.length > 0) {
            issueData.put("labels", labels);
        }

        RequestBody reqBody = RequestBody.create(gson.toJson(issueData), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + authToken)
                .addHeader("Accept", "application/vnd.github+json")
                .post(reqBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // Parse JSON to get URL
                Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);
                String htmlUrl = (String) responseMap.get("html_url");
                logger.info("GitHub Issue created successfully: {}", htmlUrl);
                return htmlUrl;
            } else {
                logger.error("Failed to create GitHub Issue: {} {}", response.code(), response.message());
                throw new RuntimeException("Failed to create GitHub Issue");
            }
        } catch (IOException e) {
            logger.error("IOException while creating GitHub Issue", e);
            throw new RuntimeException("GitHub API call failed", e);
        }
    }
}
