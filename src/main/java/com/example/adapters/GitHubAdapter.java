package com.example.adapters;

import com.example.ports.GitHubPort;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Real implementation of the GitHub Port using OkHttp.
 * Handles authentication and JSON serialization for issue creation.
 */
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiUrl;
    private final String authToken;
    private final Gson gson;

    public GitHubAdapter(String apiUrl, String authToken) {
        this.client = new OkHttpClient(); // In prod, inject a shared client or configure timeouts
        this.apiUrl = apiUrl;
        this.authToken = authToken;
        this.gson = new Gson();
    }

    @Override
    public Optional<String> createIssue(String title, String body) {
        // Construct JSON payload
        JsonObject payload = new JsonObject();
        payload.addProperty("title", title);
        payload.addProperty("body", body);

        RequestBody requestBody = RequestBody.create(gson.toJson(payload), JSON);

        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "token " + authToken)
            .addHeader("Accept", "application/vnd.github.v3+json")
            .post(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseData = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);
                String htmlUrl = jsonResponse.get("html_url").getAsString();
                log.info("Created GitHub issue: {}", htmlUrl);
                return Optional.of(htmlUrl);
            } else {
                log.error("Failed to create issue. Code: {}, Message: {}", response.code(), response.message());
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("IO Exception while calling GitHub API", e);
            return Optional.empty();
        }
    }
}
