package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates the creation of an issue and captures the request.
 */
public class MockGitHubClient implements GitHubPort {

    private final List<Command> receivedCommands = new ArrayList<>();
    private String responseUrl = "https://github.com/example/repo/issues/1";

    @Override
    public String createIssue(Command cmd) {
        this.receivedCommands.add(cmd);
        // Simulate returning a valid GitHub URL
        return responseUrl;
    }

    public Command getFirstCommand() {
        if (receivedCommands.isEmpty()) return null;
        return receivedCommands.get(0);
    }

    public void setResponseUrl(String url) {
        this.responseUrl = url;
    }
}