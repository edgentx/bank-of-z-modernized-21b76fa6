package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for GitHub interactions.
 * Records calls and returns predictable URLs for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<Call> calls = new ArrayList<>();
    private String mockUrlPrefix = "https://github.com/mock-org/issues/";
    private int callCount = 0;

    @Override
    public String createIssue(String title, String body) {
        calls.add(new Call(title, body));
        callCount++;
        return mockUrlPrefix + callCount;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void reset() {
        calls.clear();
        callCount = 0;
    }

    public record Call(String title, String body) {}
}
