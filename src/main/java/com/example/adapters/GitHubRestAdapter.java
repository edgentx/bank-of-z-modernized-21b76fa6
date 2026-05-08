package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

/**
 * REST Adapter for GitHub interactions.
 * Uses OkHttp and Jackson.
 */
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
        // Implementation to be completed in Green phase
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
