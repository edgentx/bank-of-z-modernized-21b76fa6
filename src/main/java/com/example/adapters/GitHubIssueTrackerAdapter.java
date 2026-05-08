package com.example.adapters;

import com.example.ports.IssueTrackerPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Adapter for GitHub Issue Tracker integration.
 * Ref: S-FB-1 Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 */
@Component
public class GitHubIssueTrackerAdapter implements IssueTrackerPort {

    @Override
    public String reportDefect(String projectId, String summary, String description) {
        // Simulate defect reporting workflow
        String issueId = UUID.randomUUID().toString();
        
        // Construct the GitHub URL for the created issue
        // Using github.com/example/bank-of-z as a placeholder base URL
        String url = "https://github.com/example/bank-of-z/issues/" + issueId;
        
        return url;
    }
}
