package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 */
@Component
public class MockGitHubPort implements GitHubPort {

    @Override
    public String createIssue(String title, String body, Map<String, String> labels) {
        System.out.println("[MockGitHub] Creating issue: " + title);
        // Return a dummy URL for testing the downstream integration
        return "https://github.com/mock/issues/123";
    }
}
