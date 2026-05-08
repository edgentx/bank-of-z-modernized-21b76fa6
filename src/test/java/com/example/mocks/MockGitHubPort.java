package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

public class MockGitHubPort implements GitHubPort {
    private boolean shouldReturnUrl = true;
    private String simulatedUrl;

    @Override
    public Optional<String> createIssue(String title, String description, String component) {
        if (shouldReturnUrl) {
            return Optional.of(simulatedUrl != null ? simulatedUrl : "https://github.com/test/issues/1");
        }
        return Optional.empty();
    }

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }
}
