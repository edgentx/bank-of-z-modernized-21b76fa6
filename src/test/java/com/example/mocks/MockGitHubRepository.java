package com.example.mocks;

import com.example.ports.GitHubRepositoryPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubRepositoryPort.
 * Returns predictable URLs for testing without calling GitHub API.
 */
public class MockGitHubRepository implements GitHubRepositoryPort {

    private final Map<String, String> responses = new HashMap<>();

    public void mockIssueUrl(String projectId, String defectId, String url) {
        String key = projectId + ":" + defectId;
        responses.put(key, url);
    }

    @Override
    public String createIssue(String projectId, String defectId, String title, String body) {
        String key = projectId + ":" + defectId;
        if (responses.containsKey(key)) {
            return responses.get(key);
        }
        // Default fallback if not explicitly mocked
        return "https://github.com/fake-repo/issues/" + defectId;
    }
}
