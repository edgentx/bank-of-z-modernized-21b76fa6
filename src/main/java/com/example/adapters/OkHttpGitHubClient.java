package com.example.adapters;

import com.example.ports.GitHubPort;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OkHttpGitHubClient implements GitHubPort {

    private final OkHttpClient client;
    private final String repoUrl;
    private final String authToken;
    private final ObjectMapper mapper = new ObjectMapper();

    public OkHttpGitHubClient(String repoUrl, String authToken) {
        this.client = new OkHttpClient();
        this.repoUrl = repoUrl;
        this.authToken = authToken;
    }

    @Override
    public String createIssue(String title, String body) {
        try {
            Map<String, Object> issueData = new HashMap<>();
            issueData.put("title", title);
            issueData.put("body", body);

            String jsonBody = mapper.writeValueAsString(issueData);

            Request request = new Request.Builder()
                    .url(repoUrl + "/issues")
                    .addHeader("Authorization", "token " + authToken)
                    .post(RequestBody.create(jsonBody, okhttp3.MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create issue: " + response.code());
                }
                // Parse response to get URL, simplistic version for now assuming JSON return
                Map<String, Object> respMap = mapper.readValue(response.body().string(), Map.class);
                return (String) respMap.get("html_url");
            }
        } catch (IOException e) {
            throw new RuntimeException("GitHub client error", e);
        }
    }
}
