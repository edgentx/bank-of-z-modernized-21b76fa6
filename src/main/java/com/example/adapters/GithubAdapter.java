package com.example.adapters;

import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

/**
 * Adapter for GitHub API interactions.
 * Uses OkHttp for HTTP requests.
 */
@Component
public class GithubAdapter {

    private final OkHttpClient client;

    public GithubAdapter(OkHttpClient client) {
        this.client = client;
    }

    public String createIssue(String repo, String title, String body) {
        // Implementation for creating GitHub issues
        return "https://github.com/example/bank-of-z/issues/1";
    }
}
