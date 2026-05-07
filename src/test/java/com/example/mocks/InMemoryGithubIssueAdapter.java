package com.example.mocks;

import com.example.ports.GithubIssuePort;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the GitHub Issue Port.
 * Simulates issue creation and URL generation without hitting GitHub API.
 */
public class InMemoryGithubIssueAdapter implements GithubIssuePort {

    private final Map<String, URI> issueUrls = new HashMap<>();
    private URI nextUrlOverride;
    private int counter = 1;

    @Override
    public String createIssue(String title, String description) {
        String issueId = "GH-" + (counter++);
        // Use override if set by test, otherwise generate default
        URI url = (nextUrlOverride != null) 
            ? nextUrlOverride 
            : URI.create("https://github.com/test-repo/issues/" + counter);
            
        issueUrls.put(issueId, url);
        return issueId;
    }

    @Override
    public URI getIssueUrl(String issueId) {
        if (!issueUrls.containsKey(issueId)) {
            throw new IllegalArgumentException("Unknown issue ID: " + issueId);
        }
        return issueUrls.get(issueId);
    }

    // Test Helper Methods

    /**
     * Allows the test to force a specific URL to be returned,
     * validating that the system correctly propagates the URL from Github to Slack.
     */
    public void setNextCreatedIssueUrl(URI url) {
        this.nextUrlOverride = url;
    }

    public void reset() {
        issueUrls.clear();
        nextUrlOverride = null;
        counter = 1;
    }
}
