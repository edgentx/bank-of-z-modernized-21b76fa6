package com.example.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

/**
 * Adapter for GitHub API using OkHttp.
 * NOTE: This class previously caused compilation errors due to missing imports.
 * The pom.xml has been updated to include OkHttp and Jackson dependencies.
 */
public class OkHttpGitHubClient {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public OkHttpGitHubClient(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public String createIssue(String owner, String repo, String title, String body) throws IOException {
        String jsonBody = mapper.writeValueAsString(new IssueRequest(title, body));
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/" + owner + "/" + repo + "/issues")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    private record IssueRequest(String title, String body) {}
}