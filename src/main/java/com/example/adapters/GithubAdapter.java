package com.example.adapters;

import com.example.ports.GithubPort;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GithubAdapter implements GithubPort {
    private final OkHttpClient client;
    private final String apiUrl;
    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public GithubAdapter(OkHttpClient client, String apiUrl, String token) {
        this.client = client;
        this.apiUrl = apiUrl;
        this.token = token;
    }

    @Override
    public String createIssue(String title, String body) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", title);
            payload.put("body", body);

            RequestBody jsonBody = RequestBody.create(
                mapper.writeValueAsString(payload),
                okhttp3.MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(apiUrl + "/issues")
                .addHeader("Authorization", "token " + token)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(jsonBody)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create issue: " + response.code());
                }
                // Extract URL from response
                String responseBody = response.body().string();
                Map<String, Object> respMap = mapper.readValue(responseBody, Map.class);
                return (String) respMap.get("html_url");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating GitHub issue", e);
        }
    }
}
