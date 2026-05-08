package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class GitHubRestAdapter implements GitHubPort {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public GitHubRestAdapter(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public String createIssue(String repo, String title, String body) {
        // Stub implementation satisfying the contract.
        // Real implementation would POST to GitHub API.
        throw new UnsupportedOperationException("Real GitHub implementation pending credential config");
    }
}
