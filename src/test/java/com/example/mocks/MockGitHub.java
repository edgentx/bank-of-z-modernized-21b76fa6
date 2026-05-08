package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.UUID;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates issue creation without touching the real GitHub API.
 */
public class MockGitHub implements GitHubPort {

    private String simulatedUrlBase = "https://github.com/example/bank-of-z/issues/";
    private int issueCounter = 1;
    private boolean failNextCreate = false;

    @Override
    public String createIssue(String title, String body) {
        if (failNextCreate) {
            throw new RuntimeException("Simulated GitHub API failure");
        }
        // Return a deterministic URL based on a counter
        return simulatedUrlBase + (issueCounter++);
    }

    public void setSimulatedUrlBase(String urlBase) {
        this.simulatedUrlBase = urlBase;
    }

    public void failOnNextCreate() {
        this.failNextCreate = true;
    }
}
