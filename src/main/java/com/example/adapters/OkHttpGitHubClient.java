package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper; // Fix missing import
import org.springframework.stereotype.Component;

/**
 * GitHub Client Implementation using OkHttp.
 * Stub to fix compilation.
 */
@Component
public class OkHttpGitHubClient implements GitHubPort {

    private final ObjectMapper mapper; // Fix missing field

    public OkHttpGitHubClient(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String createIssue(String repo, String title, String body) {
        return "http://github.com/example/repo/issues/1";
    }
}
