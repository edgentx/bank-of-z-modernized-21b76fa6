package com.example.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * HTTP Client adapter for GitHub interactions.
 * Currently a placeholder/passthrough to satisfy the build.
 */
@Component
public class OkHttpGitHubClient {

    private final ObjectMapper objectMapper;

    public OkHttpGitHubClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
