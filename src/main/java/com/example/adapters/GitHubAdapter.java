package com.example.adapters;

import com.example.ports.GitHubPort;

/**
 * Concrete implementation of GitHubPort.
 * In a real scenario, this would use GitHub's REST API to create an issue.
 */
public class GitHubAdapter implements GitHubPort {

    private static final String BASE_URL = "https://github.com/example/repo/issues/";

    @Override
    public String createIssue(String title, String body) {
        // Real implementation would involve:
        // GHRepository repo = gitHub.getRepository("owner/repo");
        // GHIssue issue = repo.createIssue(title).body(body).create();
        // return issue.getHtmlUrl().toString();

        // Simulation of issue creation for the defect verification.
        // We generate a deterministic mock URL based on a simulated ID.
        String mockId = "454";
        String url = BASE_URL + mockId;
        
        System.out.println("[GitHubAdapter] Creating issue " + mockId + " with title: " + title);
        return url;
    }
}