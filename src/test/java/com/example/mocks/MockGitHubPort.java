package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates successful issue creation and captures calls for verification.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<IssueCall> calls = new ArrayList<>();
    private String returnUrl = "https://github.com/example/project/issues/1";

    public record IssueCall(String title, String body) {}

    @Override
    public String createIssue(String title, String body) {
        calls.add(new IssueCall(title, body));
        // Simulate appending the issue number to return a unique URL per call
        return returnUrl + "-" + calls.size(); 
    }

    public List<IssueCall> getCalls() {
        return calls;
    }

    public void setReturnUrl(String url) {
        this.returnUrl = url;
    }
}